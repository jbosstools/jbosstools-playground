package org.jboss.tools.eclipse.open;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.resources.ProjectDescriptionReader;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.jboss.tools.eclipse.Activator;
import org.jboss.tools.eclipse.Messages;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;
import org.jboss.tools.eclipse.open.extension.ProjectConfiguratorExtensionManageer;
import org.xml.sax.InputSource;

public class OpenFolderCommand extends AbstractHandler implements IHandler {

	private Shell shell;
	private boolean configurationCancelled; 

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText(Messages.selectFolderToImport);
		String res = directoryDialog.open();
		if (res == null) {
			return null;
		}
		File directory = new File(res);
		String currentName = directory.getName();
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : workspaceRoot.getProjects()) {
			if (res.equals(project.getLocation().toString())) {
				MessageDialog.openInformation(shell,
						Messages.alreadyImportedAsProject_title,
						NLS.bind(Messages.alreadyImportedAsProject_description, project.getName()));
				return project;
			}
		}
		
		File expectedProjectDescriptionFile = new File(directory, IProjectDescription.DESCRIPTION_FILE_NAME);
		if (expectedProjectDescriptionFile.exists()) {
			InputStream stream = null;
			IProjectDescription projectDescription = null;
			try {
				stream = new FileInputStream(expectedProjectDescriptionFile);
				InputSource source = new InputSource(stream);
				projectDescription = new ProjectDescriptionReader().read(source);
				stream.close();
			} catch (IOException ex) {
				throw new ExecutionException(ex.getMessage(), ex);
			}
			String expectedName = projectDescription.getName();
			IProject projectWithSameName = workspaceRoot.getProject(expectedName);
			if (projectWithSameName.exists()) {
				if (projectWithSameName.getLocation().toFile().equals(directory)) {
					MessageDialog.openInformation(shell,
							Messages.alreadyImportedAsProject_title,
							NLS.bind(Messages.alreadyImportedAsProject_description, projectWithSameName.getName()));
					return projectWithSameName;
				} else {
					MessageDialog.openError(shell,
							Messages.anotherProjectWithSameNameExists_title,
							NLS.bind(Messages.anotherProjectWithSameNameExists_description, expectedName));
					return null;
				}
			} else {
				projectDescription.setLocation(new Path(directory.getAbsolutePath()));
				CreateProjectOperation operation = new CreateProjectOperation(projectDescription, NLS.bind(Messages.importProject, currentName));
				return performProjectCreationAndReturn(operation, directory.getName(), projectDescription.getName());
			}
		}
		
		while (workspaceRoot.getProject(currentName).exists()) {
			currentName += "_";
		}
		IProjectDescription desc = new ProjectDescription();
		desc.setName(currentName);
		desc.setLocation(new Path(directory.getAbsolutePath()));
		// open Configuration wizard
		CreateProjectOperation operation = new CreateProjectOperation(desc, NLS.bind(Messages.importProject, currentName));
		return performProjectCreationAndReturn(operation, directory.getName(), currentName);
	}
	
	public IProject performProjectCreationAndReturn(final CreateProjectOperation operation, String directory, String projectName) {
		Job job = new Job("Opening directory: " + directory + " as " + projectName) {
			
			@Override
			public IStatus run(IProgressMonitor monitor) {
				try {
					IStatus status = operation.execute(monitor, null);
					if (!status.isOK()) {
						return status;
					}
					IProject newProject = (IProject) operation.getAffectedObjects()[0];
					List<ProjectConfigurator> enabledConfigurators = new ArrayList<ProjectConfigurator>();
					for (ProjectConfigurator configurator : ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators()) {
						if (configurator.canApplyFor(newProject, monitor)) {
							enabledConfigurators.add(configurator);
						}
					}
					if (!enabledConfigurators.isEmpty()) {
						final SelectConfiguratorsWizard wizard = new SelectConfiguratorsWizard(newProject, enabledConfigurators);
						Display.getDefault().syncExec(new Runnable() {
							@Override
							public void run() {
								configurationCancelled = new WizardDialog(shell, wizard).open() != Dialog.OK;
							}
						});
						if (!configurationCancelled) {
							for (ProjectConfigurator configurator : wizard.getSelectedConfigurators()) {
								configurator.applyTo(newProject, monitor);
							}
						}
					}
					return Status.OK_STATUS;
				} catch (ExecutionException ex) {
					return new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex);
				}
			}
		};
		job.setUser(true);
		job.schedule();
		return null;
	}

}