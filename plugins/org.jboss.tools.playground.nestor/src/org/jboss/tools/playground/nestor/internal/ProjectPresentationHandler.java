package org.jboss.tools.playground.nestor.internal;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.jboss.tools.playground.nestor.Activator;

public class ProjectPresentationHandler extends AbstractHandler {

	private static final String NEST_PARAMETER = "org.jboss.tools.playground.nestor.projectPresentation.nest"; //$NON-NLS-1$

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		if (HandlerUtil.matchesRadioState(event)) {
			return null;
		}
	
		Boolean previousNest = isNestingEnabled();
		String newNestParam = event.getParameter(NEST_PARAMETER);
		boolean newNest = false;
		if (newNestParam != null) {
			newNest = Boolean.parseBoolean(newNestParam);
		}
		if (newNest != previousNest) {
			Activator.getDefault().getPreferenceStore().setValue(NEST_PARAMETER, newNest);;
			((ProjectExplorer)PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getActivePart()).getCommonViewer().refresh();
		}
		
		HandlerUtil.updateRadioState(event.getCommand(), Boolean.toString(newNest));
		// TODO refresh selection
		return Boolean.valueOf(newNest);
	}


	public static boolean isNestingEnabled() {
		return Activator.getDefault().getPreferenceStore().getBoolean(NEST_PARAMETER);
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

}
