package org.jboss.tools.playground.easymport.expression;

import org.eclipse.core.expressions.EvaluationResult;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IConfigurationElement;

public class HasFileWithSuffixRecursivelyExpression extends Expression {
	
	public static final String TAG = "hasFileWithSuffixRecursively";

	private String suffix;
	
	public HasFileWithSuffixRecursivelyExpression(String suffix) {
		this.suffix = suffix;
	}
	
	public HasFileWithSuffixRecursivelyExpression(IConfigurationElement element) {
		this(element.getAttribute("suffix"));
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
			RecursiveSuffixFileFinder finder = new RecursiveSuffixFileFinder();
			container.accept(finder);
			return EvaluationResult.valueOf(finder.foundFileWithSuffix());
		}
		return EvaluationResult.FALSE;
	}

	private class RecursiveSuffixFileFinder implements IResourceVisitor {

		private boolean res = false;
		
		@Override
		public boolean visit(IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE && resource.getName().endsWith(HasFileWithSuffixRecursivelyExpression.this.suffix)) {
				this.res = true;
			}
			if (this.res) {
				return false;
			}
			return resource instanceof IContainer;
		}
		
		public boolean foundFileWithSuffix() {
			return this.res;
		}
	}
}
