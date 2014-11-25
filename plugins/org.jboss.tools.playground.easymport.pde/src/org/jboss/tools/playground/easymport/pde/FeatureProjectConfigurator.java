package org.jboss.tools.playground.easymport.pde;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.pde.internal.core.feature.WorkspaceFeatureModel;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class FeatureProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		IFile featureFile = project.getFile("feature.xml");
		if (featureFile.exists()) {
			WorkspaceFeatureModel workspaceFeatureModel = new WorkspaceFeatureModel(featureFile);
			workspaceFeatureModel.load();
			return workspaceFeatureModel.isLoaded();
		}
		return featureFile.exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		if (!PDE.hasFeatureNature(project)) {
			try {
				CoreUtility.addNatureToProject(project, PDE.FEATURE_NATURE, monitor);
			} catch (Exception ex) {
				Activator.getDefault().getLog().log(new Status(
						IStatus.ERROR,
						Activator.PLUGIN_ID,
						ex.getMessage(),
						ex));
			}
		}
	}
	
	@Override
	public String getLabel() {
		return Messages.featureConfiguratorLabel;
	}

	@Override
	public boolean isProject(IContainer container, IProgressMonitor monitor) {
		return container.getFile(new Path("feature.xml")).exists();
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}

}
