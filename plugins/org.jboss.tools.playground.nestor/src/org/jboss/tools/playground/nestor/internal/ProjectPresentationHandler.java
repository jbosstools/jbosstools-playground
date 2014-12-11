/*******************************************************************************
 * Copyright (c) 2014 Red Hat Inc., and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mickael Istria (Red Hat Inc.) - initial API and implementation
 ******************************************************************************/
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

		boolean previousNest = isNestingEnabled();
		String newNestParam = event.getParameter(NEST_PARAMETER);
		boolean newNest = false;
		if (newNestParam != null) {
			newNest = Boolean.parseBoolean(newNestParam);
		}
		if (newNest != previousNest) {
			Activator.getDefault().getPreferenceStore().setValue(NEST_PARAMETER, newNest);
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
