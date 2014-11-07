package org.jboss.tools.playground.nestor.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.resources.ProjectExplorer;

public class ProjectPresentationHandler extends AbstractHandler {

	private static final String NEST_PARAMETER = "org.jboss.tools.playground.nestor.projectPresentation.nest"; //$NON-NLS-1$
	private static boolean nest = false;

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		String newNestParam = event.getParameter(NEST_PARAMETER);
		boolean newNest = false;
		if (newNestParam != null) {
			newNest = Boolean.parseBoolean(newNestParam);
		}
		if (newNest != nest) {
			nest = newNest;
			((ProjectExplorer)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()).getCommonViewer().refresh();
		}
		// TODO refresh selection
		return Boolean.valueOf(nest);
	}


	public static boolean isNestingEnabled() {
		return nest;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
