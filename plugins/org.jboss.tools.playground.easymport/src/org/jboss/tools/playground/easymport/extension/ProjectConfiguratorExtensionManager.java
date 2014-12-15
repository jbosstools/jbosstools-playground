package org.jboss.tools.playground.easymport.extension;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.eclipse.core.expressions.ElementHandler;
import org.eclipse.core.expressions.EvaluationContext;
import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.internal.expressions.ExpressionPlugin;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.services.EvaluationReference;
import org.eclipse.ui.services.IEvaluationReference;
import org.eclipse.ui.services.IEvaluationService;
import org.jboss.tools.playground.easymport.Activator;
import org.jboss.tools.playground.easymport.expression.FileExpressionHandler;


public class ProjectConfiguratorExtensionManager {

	private static final String EXTENSION_POINT_ID = Activator.PLUGIN_ID + ".projectConfigurators"; //$NON-NLS-1$

	private static ProjectConfiguratorExtensionManager INSTANCE;
	
	private IConfigurationElement[] extensions;
	private ExpressionConverter expressionConverter;
	private Map<IConfigurationElement, ProjectConfigurator> configuratorsByExtension = new HashMap<IConfigurationElement, ProjectConfigurator>();
	
	/**
	 * Made private to have a singleton.
	 * Each instance of this class will have it's own internal registry, that will load (maximum) once each extension class,
	 * depending on whether the extension has been active for one case handled by this Manager.
	 */
	public ProjectConfiguratorExtensionManager() {
		this.extensions = Platform.getExtensionRegistry().getConfigurationElementsFor(EXTENSION_POINT_ID);
		this.expressionConverter = new ExpressionConverter(new ElementHandler[] {
			ElementHandler.getDefault(),
			new FileExpressionHandler()
		});
	}
	
	public Collection<ProjectConfigurator> getAllActiveProjectConfigurators(IContainer container) {
		Set<ProjectConfigurator> res = new HashSet<ProjectConfigurator>();
		for (IConfigurationElement extension : this.extensions) {
			IConfigurationElement[] activeWhenElements = extension.getChildren("activeWhen");
			if (activeWhenElements.length == 0) {
				// by default, if no activeWhen, enable extension
				res.add(getConfigurator(extension));
			} else if (activeWhenElements.length == 1) {
				IConfigurationElement activeWhen = activeWhenElements[0];
				IConfigurationElement[] activeWhenChildren = activeWhen.getChildren();
				if (activeWhenChildren.length == 1) {
					try {
						Expression expression = this.expressionConverter.perform(activeWhen.getChildren()[0]); 
						IEvaluationContext context = new EvaluationContext(null, container);
						if (expression.evaluate(context).equals(EvaluationResult.TRUE)) {
							res.add(getConfigurator(extension));
						}
					} catch (CoreException ex) {
						Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, "Could not evaluate expression for " + extension.getContributor().getName(), ex));
					}
				} else {
					Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID,
							"Could not evaluate xpression for " + extension.getContributor().getName() + ": there must be exactly one child of 'activeWhen'"));
				}
			} else {
				throw new IllegalArgumentException("Only one 'activeWhen' is authorized on extension point " + EXTENSION_POINT_ID + ", for extension contributed by " +
						extension.getContributor().getName());
			}
		}
		return res;
	}
	
	private ProjectConfigurator getConfigurator(IConfigurationElement extension) {
		if (!this.configuratorsByExtension.containsKey(extension)) {
			try {
				ProjectConfigurator configurator = (ProjectConfigurator) extension.createExecutableExtension("class"); //$NON-NLS-1$
				this.configuratorsByExtension.put(extension, configurator);
				return configurator;
			} catch (CoreException ex) {
				Activator.getDefault().getLog().log(new Status(IStatus.ERROR, Activator.PLUGIN_ID, ex.getMessage(), ex));
				return null;
			}
		} else {
			return this.configuratorsByExtension.get(extension);
		}
		
	}
		
	
}
