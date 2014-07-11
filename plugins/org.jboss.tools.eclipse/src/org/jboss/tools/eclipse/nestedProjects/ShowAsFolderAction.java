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

import org.eclipse.core.resources.IFolder;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.osgi.util.NLS;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonViewer;
import org.jboss.tools.eclipse.Messages;

/**
 * @since 3.3
 *
 */
public class ShowAsFolderAction extends Action {

	private CommonViewer viewer;
	private IFolder folder;

	public ShowAsFolderAction(IFolder folder, CommonViewer viewer) {
		super(NLS.bind(Messages.ShowAsFolder, folder.getName()));
		this.folder = folder;
		this.viewer = viewer;
		setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDER));
	}
	
	public void run() {
		NestedProjectManager.unregisterProjectShownInFolder(folder);
		viewer.refresh();
		viewer.refresh(folder.getParent());
		viewer.setSelection(new StructuredSelection(new Object[] { folder }));
	}
}
