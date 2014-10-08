/*******************************************************************************
 * Copyright (c) 2014 Red Hat Inc.
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mickael Istria (Red Hat Inc.) - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.eclipse.nestedProjects;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.TreeNode;
import org.eclipse.jface.viewers.TreePath;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.ui.IWorkingSet;

/**
 * Hides project if it is shown nested in some other location
 * @author mistria
 *
 */
public class HideTopLevelProjectIfNested extends ViewerFilter {

	@Override
	public boolean select(Viewer viewer, Object parentElement, Object element) {
		if (element instanceof IProject) {
			if (parentElement != null) {
				Object parentObject = null;
				if (parentElement instanceof TreeNode) {
					parentObject = ((TreeNode)parentElement).getValue();
				} else if (parentElement instanceof TreePath) {
					parentObject = ((TreePath)parentElement).getLastSegment();
				} else {
					parentObject = parentElement;
				}
				if (parentObject instanceof IAdaptable) {
					IAdaptable parentAdaptable = (IAdaptable)parentObject;
					if (parentAdaptable.getAdapter(IWorkspaceRoot.class) != null ||
						parentAdaptable.getAdapter(IWorkingSet.class) != null) {
						return !NestedProjectManager.isShownAsNested((IProject)element);
					}
				}
			}
		}
		return true;
	}

}
