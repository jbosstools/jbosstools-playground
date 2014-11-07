/*******************************************************************************
 * Copyright (c) 2014 Ivica Loncar, and others
 * All rights reserved. This program and the accompanying materials
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributors:
 *     Ivica Loncar - Projects open from inside parent inherit working sets
 ******************************************************************************/
package org.jboss.tools.playground.nestor.internal;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.IWorkingSetManager;
import org.eclipse.ui.PlatformUI;

public class WorkingSets {

	public static void addToWorkingSets(IProject project, Set<IWorkingSet> workingSets) {
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkingSetManager manager = workbench.getWorkingSetManager();
		manager.addToWorkingSets(project, workingSets.toArray(new IWorkingSet[workingSets.size()]));
	}

	public static Set<IWorkingSet> projectWorkingSets(IProject project) {
		Set<IWorkingSet> projectWorkingSets = new HashSet<IWorkingSet>();
		IWorkbench workbench = PlatformUI.getWorkbench();
		IWorkingSetManager manager = workbench.getWorkingSetManager();
		IWorkingSet[] allWorkingSets = manager.getAllWorkingSets();
		for (IWorkingSet workingSet : allWorkingSets) {
			IAdaptable[] elements = workingSet.getElements();
			for (IAdaptable element : elements) {
				if (element instanceof IProject) {
					if (project.equals(element)) {
						projectWorkingSets.add(workingSet);
					}
				}
			}
		}
		return projectWorkingSets;
	}
}
