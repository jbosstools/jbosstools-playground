package org.jboss.tools.playground.easymport.cordova;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.thym.core.natures.HybridAppNature;
import org.eclipse.thym.core.platform.PlatformConstants;
import org.eclipse.ui.wizards.datatransfer.ProjectConfigurator;
import org.eclipse.wst.jsdt.core.JavaScriptCore;

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
		try {
			IProjectDescription description = project.getDescription();
		    String[] oldNatures = description.getNatureIds();
		    List<String> natureList =  new ArrayList<String>();
		    natureList.addAll(Arrays.asList(oldNatures));
		    
			if( !project.hasNature(HybridAppNature.NATURE_ID ) ){
				natureList.add(HybridAppNature.NATURE_ID);
			}
			
			if( !project.hasNature( JavaScriptCore.NATURE_ID )){
				natureList.add(JavaScriptCore.NATURE_ID);
			}
			
		    description.setNatureIds(natureList.toArray(new String[natureList.size()]));
		    project.setDescription(description, monitor);
		} catch (CoreException ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.getDefault().getBundle().getSymbolicName(),
					ex.getMessage(),
					ex));
		}
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}
}
