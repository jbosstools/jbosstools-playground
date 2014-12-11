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
import org.eclipse.core.runtime.IPath;
import org.jboss.tools.playground.nestor.internal.ProjectPresentationHandler;

/**
 * @since 3.3
 *
 */
public class NestedProjectManager {

	/**
	 * (1) {@code ProjectPresentationHandler.isNestingEnabled()} returns {@code true}, returns an {@link IProject} having the same location as the given {@code folder}
	 * or {@code null} if no such exists.
	 * <p>
	 * (2) {@code ProjectPresentationHandler.isNestingEnabled()} returns {@code false}, returns {@code null}.

	 * @param folder a folder to decide about
	 * @return an {@link IProject} or {@code null}
	 */
	public static IProject getProject(IFolder folder) {
		if (folder == null) {
			return null;
		}
		if (ProjectPresentationHandler.isNestingEnabled()) {
			IPath folderLocation = folder.getLocation();
			// FIXME: performance: this is probably called often enough to cache the folder -> project mapping?
			for (IProject project : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				if (project.getLocation().equals(folderLocation)) {
					return project;
				}
			}
		}
		return null;
	}

	/**
	 * A shorthand for {@code getProject(folder) != null}.
	 *
	 * @param folder
	 * @return {@code true} if project having the same location as {@code folder} exists and nesting is enabled, {@code false} otherwise
	 */
	public static boolean isShownAsProject(IFolder folder) {
		return getProject(folder) != null;
	}

	public static boolean isShownAsNested(IProject project) {
		if (ProjectPresentationHandler.isNestingEnabled()) {
			IPath queriedLocation = project.getLocation();
			// FIXME: performance: this is probably called often enough to cache the project -> parentProject mapping?
			for (IProject otherProject : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
				IPath otherLocation = otherProject.getLocation();
				if (otherProject.isOpen() && queriedLocation.segmentCount() - otherLocation.segmentCount() > 0 && otherLocation.isPrefixOf(queriedLocation)) {
					/* otherLocation is ancestor of queriedLocation (but not equal) */
					return true;
				}
			}
		}
		return false;
	}
	
	public static IFolder getMostDirectOpenContainer(IProject project) {
		IProject mostDirectParentProject = null;
		for (IProject other : ResourcesPlugin.getWorkspace().getRoot().getProjects()) {
			if (!project.equals(other) && other.isOpen()) {
				IPath otherLocation = other.getLocation();
				if ((mostDirectParentProject == null || otherLocation.segmentCount() > mostDirectParentProject.getLocation().segmentCount())
					&& other.getLocation().isPrefixOf(project.getLocation())) {
					mostDirectParentProject = other;
				}
			}
		}
		if (mostDirectParentProject != null) {
			return mostDirectParentProject.getFolder(project.getLocation().removeLastSegments(1).removeFirstSegments(mostDirectParentProject.getLocation().segmentCount()));
		}
		return null;
	}

}
