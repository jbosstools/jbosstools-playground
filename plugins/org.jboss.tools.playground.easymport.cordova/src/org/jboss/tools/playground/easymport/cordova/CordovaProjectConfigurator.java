package org.jboss.tools.playground.easymport.cordova;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.swt.widgets.Display;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;

public class CordovaProjectConfigurator implements ProjectConfigurator {


	@Override
	public boolean shouldBeAnEclipseProject(IContainer container, IProgressMonitor monitor) {
		return false;
	}

	@Override
	public boolean canConfigure(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		return project.getFile(PlatformConstants.FILE_XML_CONFIG).exists() || project.getFolder(PlatformConstants.DIR_WWW).getFile(PlatformConstants.FILE_XML_CONFIG).exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void configure(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				MessageDialog.openWarning(Display.getDefault().getActiveShell(), "Cordova config not yet ready", "Sorry, but Cordova configuration is not yet ready. But showing Cordova project detection was cool for a demo, wasn't it?");
			}
		});
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}
}
