package org.jboss.tools.eclipse.extension;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.wizard.IWizardPage;

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
	 * This method MUST BE be stateless (ideally static)
	 * @param projectDescription
	 * @return
	 */
	public boolean canApplyFor(IProjectDescription projectDescription, IProgressMonitor monitor);
	
	public Collection<IWizardPage> getConfigurationWizardPages();
	
	public void applyTo(IProject project, IProgressMonitor monitor);
	
}
