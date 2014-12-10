package org.jboss.tools.playground.easymport.extension;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.playground.easymport.Messages;

public class EclipseProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean shouldBeAnEclipseProject(IContainer container, IProgressMonitor monitor) {
		return container.getFile(new Path(IProjectDescription.DESCRIPTION_FILE_NAME)).exists();
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}

	@Override
	public boolean canConfigure(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor) {
		return true;
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void configure(IProject project, Set<IPath> excludedDirectories, IProgressMonitor monitor) {
		// Nothing to do
	}

	@Override
	public String getLabel() {
		return Messages.eclipseProjectConfigurationLabel;
	}

}
