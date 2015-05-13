/*******************************************************************************
 * Copyright (c) 2015 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 *  Contributors:
 *       Red Hat, Inc. - initial API and implementation
 *******************************************************************************/
package org.jboss.tools.playground.bower.internal.preferences;

import java.io.File;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.jface.preference.DirectoryFieldEditor;
import org.eclipse.jface.preference.FieldEditorPreferencePage;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.jboss.tools.playground.bower.internal.Activator;
import org.jboss.tools.playground.bower.internal.BowerConstants;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class BowerPreferencePage extends FieldEditorPreferencePage implements IWorkbenchPreferencePage {
	public static final String PAGE_ID = "org.jboss.tools.playground.bower.internal.preferences.BowerPreferencesPage"; //$NON-NLS-1$
	private BowerHomeFieldEditor bowerEditor;
	
	public BowerPreferencePage() {
		super(GRID);
		setPreferenceStore(Activator.getDefault().getPreferenceStore());
		setDescription("npm Settings for Bower Support"); //$NON-NLS-1$
	}
	
	@Override
	public void init(IWorkbench workbench) {
	}
	
	@Override
	public boolean performOk() {
		super.performOk();
		String filePath = bowerEditor.getTextControl(getFieldEditorParent()).getText();
		BowerPreferenceHolder.setNodeLocation(new File(filePath.trim()).getAbsolutePath());
		return true;
	}

	@Override
	protected void createFieldEditors() {
		bowerEditor = new BowerHomeFieldEditor("npm Home", "npm Location", getFieldEditorParent()); //$NON-NLS-1$ //$NON-NLS-2$
		addField(bowerEditor);
	}
	
	private static class BowerHomeFieldEditor extends DirectoryFieldEditor {
		
		public BowerHomeFieldEditor(String key, String label, Composite composite) {
			super(key, label, composite);
			setEmptyStringAllowed(true);
		}
		
		@Override
		protected boolean doCheckState() {
			String filename = getTextControl().getText();
			filename = filename.trim();
			if (filename.isEmpty()) {
				this.getPage().setMessage("A location for the npm must be specified", IStatus.WARNING); //$NON-NLS-1$
				return true;
			} else {
				// clear the warning message
				this.getPage().setMessage(null);
			}

			if (!filename.endsWith(File.separator)) {
				filename = filename + File.separator;
			}
			
			File selectedFile = new File(filename);
			File nodeModules = new File(selectedFile, BowerConstants.NODE_MODULES);
			if (nodeModules == null || !nodeModules.exists()) {
				setErrorMessage("Not valid npm location"); //$NON-NLS-1$
				return false;
			}
			
			File bower = new File(selectedFile, BowerConstants.BOWER);
			if (bower == null || !bower.exists()) {
				setErrorMessage("bower must be installed. Visit <a href='http://bower.io/docs/api/'>http://bower.io/docs/api/</a>"); //$NON-NLS-1$
				return false;
			}
			return true;
		}
		
		@Override
		public void setValidateStrategy(int value) {
			super.setValidateStrategy(VALIDATE_ON_KEY_STROKE);
		}
		
	}

}
