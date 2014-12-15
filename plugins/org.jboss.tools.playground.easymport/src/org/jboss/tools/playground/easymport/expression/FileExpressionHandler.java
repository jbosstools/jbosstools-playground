package org.jboss.tools.playground.easymport.expression;

import org.eclipse.core.expressions.ElementHandler;
import org.eclipse.core.expressions.Expression;
import org.eclipse.core.expressions.ExpressionConverter;
import org.eclipse.core.expressions.ExpressionTagNames;
import org.eclipse.core.internal.expressions.ExpressionMessages;
import org.eclipse.core.internal.expressions.ExpressionPlugin;
import org.eclipse.core.internal.expressions.InstanceofExpression;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.w3c.dom.Element;

public class FileExpressionHandler extends ElementHandler {

	@Override
	public Expression create(ExpressionConverter converter, IConfigurationElement element) throws CoreException {
		String name = element.getName();
		if (HasFileExpression.TAG.equals(name)) {
			return new HasFileExpression(element);
		} else if (HasFileRecursivelyExpression.TAG.equals(name)) {
			return new HasFileRecursivelyExpression(element);
		} else if (HasFileWithSuffixRecursivelyExpression.TAG.equals(name)) {
			return new HasFileWithSuffixRecursivelyExpression(element);
		}
		return null;
	}
}
