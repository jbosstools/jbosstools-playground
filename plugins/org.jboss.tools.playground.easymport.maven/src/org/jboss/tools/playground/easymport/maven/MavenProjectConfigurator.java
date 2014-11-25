package org.jboss.tools.playground.easymport.maven;

import java.util.HashSet;
import java.util.Set;

import org.apache.maven.model.Model;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class MavenProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor) {
		return isProject(project, monitor); 
	}

	@Override
	public IWizard getConfigurationWizard() {
		// no need for a wizard, will just set up the m2e nature
		return null;
	}

	@Override
	public void applyTo(IProject project, Set<IPath> excludedDirectories, IProgressMonitor monitor) {
		// copied from org.eclipse.m2e.core.ui.internal.actions.EnableNatureAction
		
		ResolverConfiguration configuration = new ResolverConfiguration();
        configuration.setResolveWorkspaceProjects(true);
        IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
        try {
	        if(!project.hasNature(IMavenConstants.NATURE_ID)) {
	        	configurationManager.enableMavenNature(project, configuration, monitor);
	        }
	        configurationManager.updateProjectConfiguration(project, monitor);
	        // TODO (if not done automatically), invoke all AbstractProjectConfigurator
        } catch (Exception ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}

	@Override
	public String getLabel() {
		return Messages.mavenConfiguratorLabel;
	}

	@Override
	public boolean isProject(IContainer container, IProgressMonitor monitor) {
		IFile pomFile = container.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		if (!pomFile.exists()) {
			return false;
		}
		try {
			Model pomModel = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
			return !pomModel.getPackaging().equals("pom"); // TODO find symbol for "pom"
		} catch (CoreException ex) {
			Activator.log(IStatus.ERROR, "Could not parse pom file " + pomFile.getLocation(), ex);
			return false;
		}
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		Set<IFolder> res = new HashSet<IFolder>();
		res.add(project.getFolder("target")); // TODO: get this value from pom.
		return res;
	}

}
