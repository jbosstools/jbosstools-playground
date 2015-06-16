package org.jboss.tools.playground.easymport.maven;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
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
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;

public class MavenProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canConfigure(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor) {
		return shouldBeAnEclipseProject(project, monitor);
	}

	@Override
	public IWizard getConfigurationWizard() {
		// no need for a wizard, will just set up the m2e nature
		return null;
	}

	@Override
	public void configure(final IProject project, Set<IPath> excludedDirectories, final IProgressMonitor monitor) {
		// copied from org.eclipse.m2e.core.ui.internal.actions.EnableNatureAction

		final ResolverConfiguration configuration = new ResolverConfiguration();
        configuration.setResolveWorkspaceProjects(true);
        final IProjectConfigurationManager configurationManager = MavenPlugin.getProjectConfigurationManager();
        try {
	        if(!project.hasNature(IMavenConstants.NATURE_ID)) {
	        	IProjectDescription description = project.getDescription();
	        	String[] prevNatures = description.getNatureIds();
	        	String[] newNatures = new String[prevNatures.length + 1];
	        	System.arraycopy(prevNatures, 0, newNatures, 1, prevNatures.length);
	        	newNatures[0] = IMavenConstants.NATURE_ID;
	        	description.setNatureIds(newNatures);
	        	project.setDescription(description, monitor);
	        }
        } catch (Exception ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}

	@Override
	public boolean shouldBeAnEclipseProject(IContainer container, IProgressMonitor monitor) {
		IFile pomFile = container.getFile(new Path(IMavenConstants.POM_FILE_NAME));
		return pomFile.exists();
		// debated on m2e-dev: https://dev.eclipse.org/mhonarc/lists/m2e-dev/msg01852.html
//		if (!pomFile.exists()) {
//			return false;
//		}
//		try {
//			Model pomModel = MavenPlugin.getMavenModelManager().readMavenModel(pomFile);
//			return !pomModel.getPackaging().equals("pom"); // TODO find symbol for "pom"
//		} catch (CoreException ex) {
//			Activator.log(IStatus.ERROR, "Could not parse pom file " + pomFile.getLocation(), ex);
//			return false;
//		}
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		Set<IFolder> res = new HashSet<IFolder>();
		// TODO: get these values from pom/project config
		res.add(project.getFolder("src"));
		res.add(project.getFolder("target"));
		return res;
	}

}
