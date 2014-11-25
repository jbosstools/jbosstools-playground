package org.jboss.tools.playground.easymport.extension;

import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizard;

/**
 * This interface contains a set of methods that allow to
 * configure an existing or future project, for example to
 * add and configure natures when creating a new project.
 * 
 * It is typically used as a:
 * <ul>
 * <li>a filter to check whether the current {@link ProjectConfigurator} can apply</li>
 * <li>a bean to store user configuration while showing wizard page</li>
 * </ul> 
 * @author mistria
 *
 */
public interface ProjectConfigurator {
	
	/**
	 * This method MUST BE stateless (ideally static)
	 * @param folder
	 * @param monitor
	 * @return true if the given folder is for sure to be considered as a project
	 */
	public boolean isProject(IContainer container, IProgressMonitor monitor);
	
	/**
	 * This method MUST BE stateless (ideally static)
	 * @param folder
	 * @param monitor
	 * @return	the set of children folder to ignore in import operation. Typically
	 * 			work directories such as bin/ target/ .... 
	 */
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor);
	
	/**
	 * This method MUST BE be stateless (ideally static)
	 * @param project
	 * @param ignoredPaths	paths that have to be ignore when checking whether configurator applies.
	 * 						Those will typically be nested projects (handled separately), or "work"
	 * 						directories (bin/ target/ ...)
	 * @param monitor
	 * @return true if the current configurator can configure given project
	 */
	public boolean canApplyFor(IProject project, Set<IPath> ignoredPaths, IProgressMonitor monitor);
	
	/**
	 * 
	 * @return an (optional) wizard to configure the project
	 */
	public IWizard getConfigurationWizard();
	
	/**
	 * This method MUST BE be stateless (ideally static)
	 * @param project
	 * @param ignoredPaths paths that have to be ignore when checking whether configurator applies.
	 * Those will typically be nested projects, or "work" directory (bin/ target/ ...)
	 * @param monitor
	 */
	public void applyTo(IProject project, Set<IPath> excludedDirectories, IProgressMonitor monitor);
	
	public String getLabel();
	
}
