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
package org.jboss.tools.playground.autosave.util;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorReference;
import org.eclipse.ui.PlatformUI;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public final class EditorUtil {

	private EditorUtil() {
	}

	public static void saveDirtyEditors() {
		Display.getDefault().asyncExec(new Runnable() {

			@Override
			public void run() {
				IEditorReference[] references = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage().getEditorReferences();
				if (references != null) {
					NullProgressMonitor monitor = new NullProgressMonitor();
					for (IEditorReference r : references) {
						IEditorPart editor = r.getEditor(false);
						if (editor != null && editor.isDirty()) {
							editor.doSave(monitor);
						}
					}
				}
			}

		});
	}

}
