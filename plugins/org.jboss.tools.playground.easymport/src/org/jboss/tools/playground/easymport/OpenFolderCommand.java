package org.jboss.tools.playground.easymport;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.internal.resources.ProjectDescriptionReader;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;
import org.jboss.tools.playground.easymport.extension.ProjectConfiguratorExtensionManageer;
import org.xml.sax.InputSource;

public class OpenFolderCommand extends AbstractHandler {

	private Shell shell;
	private IWorkspaceRoot workspaceRoot;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		this.shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
		DirectoryDialog directoryDialog = new DirectoryDialog(shell);
		directoryDialog.setText(Messages.selectFolderToImport);
		String res = directoryDialog.open();
		if (res == null) {
			return null;
		}
		final File directory = new File(res);
		this.workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		final HashSet<IWorkingSet> workingSets = new HashSet<IWorkingSet>();
		IStructuredSelection sel = (IStructuredSelection)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService().getSelection();
		for (Object o : sel.toList()) {
			if (o instanceof IWorkingSet) {
				workingSets.add((IWorkingSet)o);
			}
		}
		try {
			PlatformUI.getWorkbench().getProgressService().run(true, true, new IRunnableWithProgress() {
				@Override
				public void run(IProgressMonitor progressMonitor) throws InvocationTargetException, InterruptedException {
					try {
						final IProject project = toExistingOrNewProject(directory, workingSets, progressMonitor);
						importProjectAndChildrenRecursively(project, true, workingSets, progressMonitor);	
					} catch (final Exception ex) {
						final Status status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), ex.getMessage(), ex);
						Activator.getDefault().getLog().log(status);
						Display.getDefault().asyncExec(new Runnable() {
							@Override
							public void run() {
								ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
										"Could not fully import " + directory.getName(),
										"An error happened while try to import " + directory.getAbsolutePath() + ": " + ex.getMessage(),
										status);								
							}
						});
					}
				}
			});
		} catch (Exception ex) {
			Status status = new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), ex.getMessage(), ex);
			Activator.getDefault().getLog().log(status);
			ErrorDialog.openError(PlatformUI.getWorkbench().getDisplay().getActiveShell(),
					"Could not fully import " + directory.getName(),
					"An error happened while try to import " + directory.getAbsolutePath() + ": " + ex.getMessage(),
					status);
		}
		return null; //TODO find something more useful to return
	}

	private Set<IProject> searchAndImportChildrenProjectsRecursively(IContainer parentContainer, Set<IPath> directoriesToExclude, Set<IWorkingSet> workingSets, IProgressMonitor progressMonitor) throws Exception {
		Set<IFolder> childrenToProcess = new HashSet<IFolder>();
		Set<IProject> res = new HashSet<IProject>();
		for (IResource childResource : parentContainer.members()) {
			if (childResource.getType() == IResource.FOLDER) {
				boolean excluded = false;
				if (directoriesToExclude != null) {
					for (IPath excludedPath : directoriesToExclude) {
						if (excludedPath.isPrefixOf(childResource.getLocation())) {
							excluded = true;
						}
					}
				}
				if (!excluded) {
					childrenToProcess.add((IFolder)childResource);
				}
			}
		}
		for (IFolder childFolder : childrenToProcess) {
			try {
				Set<IProject> projectFromCurrentContainer = importProjectAndChildrenRecursively(childFolder, false, workingSets, progressMonitor);
				res.addAll(projectFromCurrentContainer);
			} catch (CouldNotImportProjectException ex) {
				// TODO accumulate the multiple issues and present it to users after import
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.getDefault().getLog().getBundle().getSymbolicName(), ex.getMessage(), ex));
			}
		}
		return res;
	}

	/**
	 * @param folder
	 * @param workingSets
	 * @param progressMonitor
	 * @return
	 * @throws Exception
	 */
	public Set<IProject> importProjectAndChildrenRecursively(IContainer container, boolean isRootProject, Set<IWorkingSet> workingSets, IProgressMonitor progressMonitor) throws Exception {
		if (progressMonitor.isCanceled()) {
			return null;
		}
		progressMonitor.beginTask("Start configuration of project at " + container.getLocation().toFile().getAbsolutePath(), ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators().size());
		Set<IProject> projectFromCurrentContainer = new HashSet<IProject>();
		Set<ProjectConfigurator> mainProjectConfigurators = new HashSet<ProjectConfigurator>();
		Set<IPath> excludedPaths = new HashSet<IPath>();
		for (ProjectConfigurator configurator : ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators()) {
			if (progressMonitor.isCanceled()) {
				return null;
			}
			if (configurator.shouldBeAnEclipseProject(container, progressMonitor)) {
				mainProjectConfigurators.add(configurator);
			}
			progressMonitor.worked(1);
		}
		if (!mainProjectConfigurators.isEmpty()) {
			/*
			 * 1. Create project
			 * 2. Apply ensured project configurators + populate excludedPaths
			 * 3. Look recursively (ignored excluded paths)
			 * 4. Applied additional configurators
			 */
			IProject project = toExistingOrNewProject(container.getLocation().toFile(), workingSets, progressMonitor);
			projectFromCurrentContainer.add(project);
			for (ProjectConfigurator configurator : mainProjectConfigurators) {
				configurator.configure(project, excludedPaths, progressMonitor);
				excludedPaths.addAll(toPathSet(configurator.getDirectoriesToIgnore(project, progressMonitor)));
			}
			Set<IProject> allNestedProjects = searchAndImportChildrenProjectsRecursively(project, excludedPaths, workingSets, progressMonitor);
			excludedPaths.addAll(toPathSet(allNestedProjects));
			progressMonitor.beginTask("Continue configuration of project at " + container.getLocation().toFile().getAbsolutePath(), ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators().size());
			for (ProjectConfigurator additionalConfigurator : ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators()) {
				if (!mainProjectConfigurators.contains(additionalConfigurator) && additionalConfigurator.canConfigure(project, excludedPaths, progressMonitor)) {
					additionalConfigurator.configure(project, excludedPaths, progressMonitor);
					excludedPaths.addAll(toPathSet(additionalConfigurator.getDirectoriesToIgnore(project, progressMonitor)));
				}
				progressMonitor.worked(1);
			}
			projectFromCurrentContainer.addAll(allNestedProjects);
		} else {
			Set<IProject> nestedProjects = searchAndImportChildrenProjectsRecursively(container, null, workingSets, progressMonitor);
			projectFromCurrentContainer.addAll(nestedProjects);
			if (nestedProjects.isEmpty() && isRootProject) {
				// No sub-project found, so apply available configurators anyway
				progressMonitor.beginTask("Configuring 'leaf' of project at " + container.getLocation().toFile().getAbsolutePath(), ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators().size());
				IProject project = toExistingOrNewProject(container.getLocation().toFile(), workingSets, progressMonitor);
				projectFromCurrentContainer.add(project);
				for (ProjectConfigurator additionalConfigurator : ProjectConfiguratorExtensionManageer.getInstance().getAllProjectConfigurators()) {
					if (additionalConfigurator.canConfigure(project, excludedPaths, progressMonitor)) {
						additionalConfigurator.configure(project, excludedPaths, progressMonitor);
						excludedPaths.addAll(toPathSet(additionalConfigurator.getDirectoriesToIgnore(project, progressMonitor)));
					}
					progressMonitor.worked(1);
				}
			}
		}
		return projectFromCurrentContainer;
	}
	
	private Set<IPath> toPathSet(Set<? extends IContainer> resources) {
		if (resources == null || resources.isEmpty()) {
			return (Set<IPath>)Collections.EMPTY_SET;
		}
		Set<IPath> res = new HashSet<IPath>();
		for (IContainer container : resources) {
			res.add(container.getLocation());
		}
		return res;
	}
	
	/**
	 * @param directory
	 * @param workingSets
	 * @return
	 * @throws Exception
	 */
	public IProject toExistingOrNewProject(File directory, Set<IWorkingSet> workingSets, IProgressMonitor progressMonitor) throws CouldNotImportProjectException {
		try {
			progressMonitor.setTaskName("Import project at " + directory.getAbsolutePath());
			IProject project = projectAlreadyExistsInWorkspace(directory);
			if (project != null) {
				return project;
			}
	
			if (progressMonitor.isCanceled()) {
				return null;
			}
			project = createOrImportProject(directory, workingSets, progressMonitor);
			project.open(progressMonitor);
			return project;
		} catch (Exception ex) {
			throw new CouldNotImportProjectException(directory, ex);
		}
	}
	
	
	private IProject projectAlreadyExistsInWorkspace(File directory) {
		IWorkspaceRoot workspaceRoot = ResourcesPlugin.getWorkspace().getRoot();
		for (IProject project : workspaceRoot.getProjects()) {
			if (project.getLocation().toFile().getAbsoluteFile().equals(directory.getAbsoluteFile())) {
				return project;
			}
		}
		return null;
	}

	private IProject createOrImportProject(File directory, Set<IWorkingSet> workingSets, IProgressMonitor progressMonitor) throws Exception {
		IProjectDescription desc = null;
		File expectedProjectDescriptionFile = new File(directory, IProjectDescription.DESCRIPTION_FILE_NAME);
		if (expectedProjectDescriptionFile.exists()) {
			InputStream stream = null;
			stream = new FileInputStream(expectedProjectDescriptionFile);
			InputSource source = new InputSource(stream);
			desc = new ProjectDescriptionReader().read(source);
			stream.close();
			String expectedName = desc.getName();
			IProject projectWithSameName = this.workspaceRoot.getProject(expectedName);
			if (projectWithSameName.exists()) {
				if (projectWithSameName.getLocation().toFile().equals(directory)) {
					throw new Exception(NLS.bind(Messages.anotherProjectWithSameNameExists_description, expectedName));
				}
			}
		} else {
			String currentName = directory.getName();
			while (this.workspaceRoot.getProject(currentName).exists()) {
				currentName += "_";
			}
			desc = new ProjectDescription();
			desc.setName(currentName);
		}
		desc.setLocation(new Path(directory.getAbsolutePath()));
		IProject res = workspaceRoot.getProject(desc.getName());
		// TODO? open Configuration wizard
		res.create(desc, progressMonitor);
		return res; 
	}

}