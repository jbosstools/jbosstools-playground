/*******************************************************************************
 * Copyright (c) 2014 Red Hat Inc., and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Mickael Istria (Red Hat Inc.) - initial API and implementation
 *     Ivica Loncar - Projects open from inside parent inherit working sets
 ******************************************************************************/
package org.jboss.tools.playground.nestor;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.jboss.tools.playground.nestor.internal.ProjectPresentationHandler;

/**
 * @since 3.3
 *
 */
public class NestedProjectManager {

	public static boolean isShownAsProject(IFolder folder) {
		if (ProjectPresentationHandler.isNestingEnabled()) {
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if (project.getLocation().equals(folder.getLocation())) {
					return true;
				}
			}
		}
		return false;
	}

	public static boolean isShownAsNested(IProject project) {
		if (ProjectPresentationHandler.isNestingEnabled()) {
			for (IProject otherProject : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if (!project.equals(otherProject) && otherProject.getLocation().isPrefixOf(project.getLocation())) {
					return true;
				}
			}
		}
		return false;
	}

}
