package org.jboss.tools.playground.easymport.cordova;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class CordovaProjectConfigurator implements ProjectConfigurator {



	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		return false;
//		return project.getFile(PlatformConstants.FILE_XML_CONFIG).exists() || project.getFolder(PlatformConstants.DIR_WWW).getFile(PlatformConstants.FILE_XML_CONFIG).exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
	}
	
	@Override
	public String getLabel() {
		return Messages.cordovaConfiguratorLabel;
	}

}
