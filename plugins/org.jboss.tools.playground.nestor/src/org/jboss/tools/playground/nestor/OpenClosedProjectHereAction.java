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
package org.jboss.tools.playground.nestor;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorPlugin;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @since 3.3
 *
 */
public class OpenClosedProjectHereAction extends Action {

	private CommonViewer viewer;
	private IFolder targetFolder;
	private IProject project;

	public OpenClosedProjectHereAction(IFolder targetFolder, IProject project, CommonViewer viewer) {
		super(Messages.OpenProjectHere);
		this.targetFolder = targetFolder;
		this.project = project;
		this.viewer = viewer;
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(SharedImages.IMG_OBJ_PROJECT));
	}
	
	public void run() {
		Job job = new Job(Messages.OpenProjectHere) {
			@Override
			public IStatus run(IProgressMonitor monitor) {
				try {
					project.open(monitor);
					NestedProjectManager.registerProjectShownInFolder(targetFolder, project);
					viewer.getControl().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							viewer.refresh(project);
							viewer.refresh(targetFolder.getParent());
							viewer.setSelection(new StructuredSelection(project));
						}
					});
					return Status.OK_STATUS;
				} catch (Exception ex) {
					return new Status(IStatus.ERROR, WorkbenchNavigatorPlugin.PLUGIN_ID, ex.getMessage(), ex);
				}	
			}
		};
		job.setUser(true);
		job.schedule();
	}
}
