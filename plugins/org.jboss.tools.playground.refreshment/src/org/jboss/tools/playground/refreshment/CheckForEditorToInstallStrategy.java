package org.jboss.tools.playground.refreshment;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.util.AbstractMap.SimpleEntry;
import java.util.HashSet;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.equinox.internal.p2.ui.ProvUI;
import org.eclipse.equinox.internal.p2.ui.model.ProfileSnapshots;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.equinox.p2.core.IProvisioningAgent;
import org.eclipse.equinox.p2.engine.IProfileRegistry;
import org.eclipse.equinox.p2.metadata.IInstallableUnit;
import org.eclipse.equinox.p2.operations.InstallOperation;
import org.eclipse.equinox.p2.query.IQueryResult;
import org.eclipse.equinox.p2.query.QueryUtil;
import org.eclipse.equinox.p2.repository.metadata.IMetadataRepository;
import org.eclipse.equinox.p2.ui.LoadMetadataRepositoryJob;
import org.eclipse.equinox.p2.ui.ProvisioningUI;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorDescriptor;
import org.eclipse.ui.IEditorRegistry;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IUnknownEditorStrategy;
import org.eclipse.ui.internal.EditorReference;
import org.eclipse.ui.internal.ide.SystemEditorOrTextEditorStrategy;

import com.eclipsesource.json.Json;
import com.eclipsesource.json.JsonArray;
import com.eclipsesource.json.JsonObject;
import com.eclipsesource.json.JsonValue;

public class CheckForEditorToInstallStrategy implements IUnknownEditorStrategy {

	private final class InstallationRunnable implements IRunnableWithProgress {
		private final ProvisioningUI provUI;
		private String fileName;
		private Shell shell;
		private boolean onlyAdditions;

		private InstallationRunnable(String fileName, Shell shell, ProvisioningUI provUI) {
			this.fileName = fileName;
			this.shell = shell;
			this.provUI = provUI;
		}

		@Override
		public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
			monitor.beginTask("Reading registry to decide what to install", 1);
			Entry<String, Set<String>> toInstall = resolveInstallationPlan(this.fileName);
			if (toInstall == null) {
				MessageDialog.openError(shell, "Could not find support", "Couldn't find an Eclipse editor for " + fileName);;
			} else {
				try {
					monitor.beginTask("Computing installation plan.", 1);
					// using monitor instead of NullProgressMonitor on line below often triggers InvalidThreadAccess...
					IMetadataRepository repo = provUI.loadMetadataRepository(new URI(toInstall.getKey()), false, new NullProgressMonitor());
					Set<IInstallableUnit> ius = new HashSet<>();
					for (String iuId : toInstall.getValue()) {
						ius.addAll(repo.query(QueryUtil.createIUQuery(iuId), monitor).toSet());
					}
					monitor.beginTask("Installing (check other dialogs)", 1);
					URI[] knownRepos = provUI.getRepositoryTracker().getKnownRepositories(provUI.getSession());
					URI[] reposForInstall = new URI[knownRepos.length + 1];
					System.arraycopy(knownRepos, 0, reposForInstall, 0, knownRepos.length);
					reposForInstall[knownRepos.length] = repo.getLocation();
					InstallOperation op = provUI.getInstallOperation(ius, reposForInstall);
					if (provUI.openInstallWizard(ius, op, new LoadMetadataRepositoryJob(provUI)) == Dialog.OK) {
						IQueryResult<IInstallableUnit> removedUnits = op.getProvisioningPlan().getRemovals().query(QueryUtil.createIUAnyQuery(), new NullProgressMonitor());
						this.onlyAdditions = removedUnits.isEmpty();
					}
				} catch (Exception ex) {
					throw new InvocationTargetException(ex);
				}
			}		
		}

		public boolean canTryReloading() {
			return this.onlyAdditions;
		}
	}

	@Override
	public IEditorDescriptor getEditorDescriptor(String fileName, IEditorRegistry editorRegistry) {
		ProgressMonitorDialog dialog = new ProgressMonitorDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
		dialog.open();
		final ProvisioningUI provUI = ProvisioningUI.getDefaultUI();
		final IProfileRegistry registry = ProvUI.getProfileRegistry(provUI.getSession());
		long[] timestamps = registry.listProfileTimestamps(provUI.getProfileId());
		final long timestampBeforeInstall = (timestamps.length == 0) ? -1 : timestamps[timestamps.length - 1];
		InstallationRunnable installRunnable = new InstallationRunnable(fileName, dialog.getShell(), provUI);
		try {
			dialog.run(false, false, installRunnable);
		} catch (Exception ex) {
			// TODO
			ex.printStackTrace();
		}

		if (installRunnable.canTryReloading()) {
			try {
				dialog.run(true, false, new IRunnableWithProgress() {
					private long getLastTimestamp() {
						long[] timestamps = registry.listProfileTimestamps(provUI.getProfileId());
						return (timestamps.length == 0) ? -1 : timestamps[timestamps.length - 1];
					}
					
					@Override
					public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
						monitor.beginTask("Wait for installation to complete", 1);
						while (getLastTimestamp() <= timestampBeforeInstall && provUI.hasScheduledOperations()) {
							Thread.sleep(500);
						}
						Thread.sleep(2016); // magic number!
						monitor.beginTask("Applying changes", 1);
						Configurator configurator = PlatformUI.getWorkbench().getService(Configurator.class);
						try {
							configurator.applyConfiguration();
						} catch (Exception ex) {
							throw new InvocationTargetException(ex);
						}
					}
				});
			} catch (Exception ex) {
				// TODO
				ex.printStackTrace();
			}
		}

		dialog.close();
		IEditorDescriptor descriptor = PlatformUI.getWorkbench().getEditorRegistry().getDefaultEditor(fileName);
		if (descriptor == null) { // installation didn't contribute an associated editor
			descriptor = new SystemEditorOrTextEditorStrategy().getEditorDescriptor(fileName, editorRegistry);
		}
		return descriptor;
	}

	private Entry<String, Set<String>> resolveInstallationPlan(String fileName) {
		InputStream stream = getClass().getResourceAsStream("editorRegistry.json");
		JsonObject best = null;
		try {
			JsonArray registry = (JsonArray)Json.parse(new InputStreamReader(stream));
			stream.close();
			String bestRegexp = null;
			for (JsonValue it : registry) {
				JsonObject entry = (JsonObject)it;
				String regexp = toJavaRegexp(entry.get("fileName").asString());
				if (fileName.matches(regexp) && (best == null || regexp.length() > bestRegexp.length())) {
					best = entry;
					bestRegexp = regexp;
				}
			}
		} catch (Exception ex) {
			ex.printStackTrace();
		}
		if (best != null) {
			JsonArray unitsArray = best.get("units").asArray();
			Set<String> units = new HashSet<>(unitsArray.size());
			for (int i = 0; i < unitsArray.size(); i++) {
				units.add(unitsArray.get(i).asString());
			}
			return new SimpleEntry<String, Set<String>>(best.get("site").asString(), units);
		}
		return null;
	}

	private String toJavaRegexp(String string) {
		return string.replace(".", "\\.").replace("*", ".*");
	}

}
