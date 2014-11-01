package org.jboss.tools.playground.easymport.maven;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.m2e.core.MavenPlugin;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.project.IProjectConfigurationManager;
import org.eclipse.m2e.core.project.ResolverConfiguration;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class MavenProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		return project.getFile(IMavenConstants.POM_FILE_NAME).exists(); 
	}

	@Override
	public IWizard getConfigurationWizard() {
		// no need for a wizard, will just set up the m2e nature
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
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
}
