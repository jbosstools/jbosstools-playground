package org.jboss.tools.playground.easymport.expression;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;
import org.jboss.tools.playground.easymport.extension.RecursiveFileFinder;

public class HasFileRecursivelyExpression extends Expression {
	
	public static final String TAG = "hasFileRecursively";

	private String filename;
	
	public HasFileRecursivelyExpression(String filename) {
		this.filename = filename;
	}
	
	public HasFileRecursivelyExpression(IConfigurationElement element) {
		this(element.getAttribute("filename"));
	}
	
	@Override
	public EvaluationResult evaluate(IEvaluationContext context) throws CoreException {
		Object root = context.getDefaultVariable();
		IContainer container = null;
		if (root instanceof IContainer) {
			container = (IContainer)root;
		} else if (root instanceof IAdaptable) {
			container = (IContainer) ((IAdaptable)root).getAdapter(IContainer.class);
		}
		if (container != null) {
			RecursiveFileFinder finder = new RecursiveFileFinder(this.filename, null);
			container.accept(finder);
			return EvaluationResult.valueOf(!finder.getFiles().isEmpty());
		}
		return EvaluationResult.FALSE;
	}

}
