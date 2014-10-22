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

import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.internal.resources.ProjectDescriptionReader;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.ide.IDE.SharedImages;
import org.eclipse.ui.ide.undo.CreateProjectOperation;
import org.eclipse.ui.internal.navigator.resources.plugin.WorkbenchNavigatorPlugin;
import org.eclipse.ui.navigator.CommonViewer;

/**
 * @since 3.3
 *
 */
public class OpenNewProjectHereAction extends Action {

	private CommonViewer viewer;
	private IFolder targetFolder;

	public OpenNewProjectHereAction(IFolder targetFolder, CommonViewer viewer) {
		super(Messages.OpenProjectHere);
		this.targetFolder = targetFolder;
		this.viewer = viewer;
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(SharedImages.IMG_OBJ_PROJECT));
	}
	
	public void run() {
		Job openProjectJob = new Job(Messages.OpenProjectHere) {
			@Override
			protected IStatus run(IProgressMonitor monitor) {
				try {
					IProjectDescription desc = new ProjectDescriptionReader().read(targetFolder.getLocation().append(IProjectDescription.DESCRIPTION_FILE_NAME));
					desc.setLocation(targetFolder.getLocation());
					CreateProjectOperation operation = new CreateProjectOperation(desc, desc.getName());
					OperationHistoryFactory.getOperationHistory().execute(operation, monitor, null);
					final IProject project = (IProject) operation.getAffectedObjects()[0];;
					NestedProjectManager.registerProjectShownInFolder(targetFolder, project);
					viewer.getControl().getDisplay().syncExec(new Runnable() {
						@Override
						public void run() {
							viewer.refresh(targetFolder);
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
		openProjectJob.setUser(true);
		openProjectJob.schedule();
	}
}
