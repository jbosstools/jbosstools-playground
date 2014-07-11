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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.internal.navigator.AdaptabilityUtility;
import org.eclipse.ui.navigator.CommonActionProvider;
import org.eclipse.ui.navigator.CommonViewer;
import org.eclipse.ui.navigator.ICommonActionExtensionSite;
import org.eclipse.ui.navigator.ICommonMenuConstants;

/**
 * @since 3.3
 *
 */
public class NestedProjectActionProvider extends CommonActionProvider {

	private CommonViewer viewer;

	@Override
	public void init(ICommonActionExtensionSite anActionSite) {
		this.viewer = (CommonViewer)anActionSite.getStructuredViewer();
	}


	public void fillContextMenu(IMenuManager aMenu) {
		IStructuredSelection selection = (IStructuredSelection) getContext().getSelection();
		if (selection.size() != 1) {
			return;	
		}
		Object object = selection.getFirstElement();
		IFolder selectedFolder = (IFolder)AdaptabilityUtility.getAdapter(object, IFolder.class);
		IProject selectedProject = (IProject)AdaptabilityUtility.getAdapter(object, IProject.class);
		if (selectedFolder != null) {
			if (! selectedFolder.getFile(IProjectDescription.DESCRIPTION_FILE_NAME).exists()) {
				return;
			}
			
			for (IProject project : selectedFolder.getWorkspace().getRoot().getProjects()) {
				if (project.getLocation().equals(selectedFolder.getLocation())) {
					Action action = null;
					if (project.isOpen()) {
						action = new ShowAsProjectAction(project, selectedFolder, this.viewer);
					} else {
						action = new OpenClosedProjectHereAction(selectedFolder, project, this.viewer);
					}
					aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, action);
					return;
				}
			}
			aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new OpenNewProjectHereAction(selectedFolder, viewer));
		} else if (selectedProject != null) {
			TreeItem parentItem = viewer.getTree().getSelection()[0].getParentItem();
			if (parentItem != null) {
				Object element = parentItem.getData();
				if (element instanceof IFolder || element instanceof IProject) {
					IContainer container = (IContainer)element;
					IFolder folder = container.getFolder(new Path(selectedProject.getLocation().lastSegment()));
					aMenu.insertAfter(ICommonMenuConstants.GROUP_OPEN, new ShowAsFolderAction(folder, viewer));
				}
			}
		}
	}
}
