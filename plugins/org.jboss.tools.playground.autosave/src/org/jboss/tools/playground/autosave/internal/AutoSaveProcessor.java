/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.playground.autosave.internal;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IPartListener;
import org.eclipse.ui.IWindowListener;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.jboss.tools.playground.autosave.util.EditorUtil;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public final class AutoSaveProcessor {
	private WorkbenchListener workbenchListener;
	private IPartListener partListener;
	
	public static final AutoSaveProcessor INSTANCE = new AutoSaveProcessor();

	private AutoSaveProcessor() {
	}

	public void enableAutoSave() {
		addListeners();
	}

	public void disableAutoSave() {
		removeListeners();
	}

	private void addListeners() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (workbenchListener == null && partListener == null) {
					workbenchListener = new WorkbenchListener();
					partListener = new PartListener();
					
					IWorkbench workbench = PlatformUI.getWorkbench();
					workbench.addWindowListener(workbenchListener);
					workbench.getActiveWorkbenchWindow().getPartService().addPartListener(partListener);
				}
			}
		});
	}

	private void removeListeners() {
		PlatformUI.getWorkbench().getDisplay().asyncExec(new Runnable() {
			public void run() {
				if (workbenchListener != null && partListener != null) {
					IWorkbench workbench = PlatformUI.getWorkbench();
					workbench.removeWindowListener(workbenchListener);
					
					IWorkbenchWindow activeWorkbenchWindow = workbench.getActiveWorkbenchWindow();
					if (activeWorkbenchWindow != null) {
						activeWorkbenchWindow.getPartService().removePartListener(partListener);
					}
					
					workbenchListener = null;
					partListener = null;
				}
			}
		});
	}

	private class WorkbenchListener implements IWindowListener {

		@Override
		public void windowDeactivated(IWorkbenchWindow window) {
			EditorUtil.saveDirtyEditors();
		}

		@Override
		public void windowClosed(IWorkbenchWindow window) {
			EditorUtil.saveDirtyEditors();
			removeListeners();
		}

		@Override
		public void windowActivated(IWorkbenchWindow window) {
		}

		@Override
		public void windowOpened(IWorkbenchWindow window) {
		}
	}
	
	private class PartListener implements IPartListener {
		
		@Override
		public void partDeactivated(IWorkbenchPart part) {
			if (part instanceof IEditorPart) {
				((IEditorPart) part).doSave(new NullProgressMonitor());
			}
		}
		
		@Override
		public void partActivated(IWorkbenchPart part) {

		}
				
		@Override
		public void partOpened(IWorkbenchPart part) {

		}

		@Override
		public void partBroughtToTop(IWorkbenchPart part) {
			
		}

		@Override
		public void partClosed(IWorkbenchPart part) {
			
		}
		
	}
	
}
