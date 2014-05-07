package org.jboss.tools.eclipse.open.extension.examples.maven;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.m2e.core.internal.M2EUtils;
import org.eclipse.m2e.core.internal.project.ProjectConfigurationManager;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;

public class MavenProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		return project.getFile("pom.xml").exists(); 
	}

	@Override
	public IWizard getConfigurationWizard() {
		// no need for a wizard, will just set up the m2e nature
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
	}

}
