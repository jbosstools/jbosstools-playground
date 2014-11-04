package org.jboss.tools.playground.easymport.cordova;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class CordovaProjectConfigurator implements ProjectConfigurator {



	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		return project.getFile(PlatformConstants.FILE_XML_CONFIG).exists() || project.getFolder(PlatformConstants.DIR_WWW).getFile(PlatformConstants.FILE_XML_CONFIG).exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Cordova config not yet ready", "Sorry, but Cordova configuration is not yet ready. But showing Cordova project detection was cool for a demo, wasn't it?");
			}
		});
	}
	
	@Override
	public String getLabel() {
		return Messages.cordovaConfiguratorLabel;
	}

}
