package org.jboss.tools.eclipse;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

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
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.xml.sax.InputSource;

public class OpenFolderCommand extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		DirectoryDialog directoryDialog = new DirectoryDialog(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell());
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
				MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
						Messages.alreadyImportedAsProject_title,
						NLS.bind(Messages.alreadyImportedAsProject_description, project.getName()));
				return project;
			}
		}
		
		IProgressMonitor monitor = new NullProgressMonitor();
		
		File expectedProjectDescriptionFile = new File(directory, IProjectDescription.DESCRIPTION_FILE_NAME);
		if (expectedProjectDescriptionFile.exists()) {
			InputStream stream = null;
			IProjectDescription projectDescription = null;
			try {
				new FileInputStream(expectedProjectDescriptionFile);
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
					MessageDialog.openInformation(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.alreadyImportedAsProject_title,
							NLS.bind(Messages.alreadyImportedAsProject_description, projectWithSameName.getName()));
					return projectWithSameName;
				} else {
					MessageDialog.openError(PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
							Messages.anotherProjectWithSameNameExists_title,
							NLS.bind(Messages.anotherProjectWithSameNameExists_description, expectedName));
					return null;
				}
			} else {
				projectDescription.setLocation(new Path(directory.getAbsolutePath()));
				CreateProjectOperation operation = new CreateProjectOperation(projectDescription, NLS.bind(Messages.importProject, currentName));
				operation.execute(monitor, null);
			}
		}
		
		while (workspaceRoot.getProject(currentName).exists()) {
			currentName += "_";
		}
		IProjectDescription desc = new ProjectDescription();
		desc.setName(currentName);
		desc.setLocation(new Path(directory.getAbsolutePath()));
		CreateProjectOperation operation = new CreateProjectOperation(desc, NLS.bind(Messages.importProject, currentName));
		operation.execute(monitor, null);
		// TODO here: show project configuration wizard
		return operation.getAffectedObjects()[0];
	}

}