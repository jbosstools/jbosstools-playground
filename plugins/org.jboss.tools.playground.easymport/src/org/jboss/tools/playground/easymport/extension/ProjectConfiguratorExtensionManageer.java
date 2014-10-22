package org.jboss.tools.playground.easymport.extension;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.jboss.tools.playground.easymport.Activator;


public class ProjectConfiguratorExtensionManageer {

	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".projectConfigurators"; //$NON-NLS-1$

	private static ProjectConfiguratorExtensionManageer INSTANCE;
	
	private Set<ProjectConfigurator> contributedProjectConfigurators;
	
	/**
	 * Made private to have a singleton
	 */
	private ProjectConfiguratorExtensionManageer() {
		this.contributedProjectConfigurators = new HashSet<ProjectConfigurator>();
		for (IConfigurationElement extension : Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID)) {
			try {
				ProjectConfigurator configurator = (ProjectConfigurator) extension.createExecutableExtension("class"); //$NON-NLS-1$
				contributedProjectConfigurators.add(configurator);
			} catch (CoreException ex) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
					ex.getMessage(), ex));
			}
		}
	}
	
	public static ProjectConfiguratorExtensionManageer getInstance() {
		if (INSTANCE == null) {
			INSTANCE = new ProjectConfiguratorExtensionManageer();
		}
		return INSTANCE;
	}
	
	public Collection<ProjectConfigurator> getAllProjectConfigurators() {
		// It would probably be better to re-instantiate them everytime
		return this.contributedProjectConfigurators;
	}
	
}
