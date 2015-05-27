package org.jboss.tools.playground.bower.internal.handler;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferenceDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.dialogs.PreferencesUtil;
import org.jboss.tools.playground.bower.internal.Messages;
import org.jboss.tools.playground.bower.internal.preferences.BowerPreferencePage;

public class UIErrorHandler {

	public static void npmLocationNotDefined() {
		boolean define = MessageDialog.openQuestion(Display.getDefault().getActiveShell(),
				Messages.ErrorHandler_NpmNotInstalledTitle, Messages.ErrorHandler_NpmNotInstalledMessage);

		if (define) {
			PreferenceDialog dialog = PreferencesUtil.createPreferenceDialogOn(Display.getDefault().getActiveShell(),
					BowerPreferencePage.PAGE_ID, null, null);
			dialog.open();
		}

	}

	public static void bowerNotInstalled() {
		MessageDialog.openWarning(Display.getDefault().getActiveShell(), Messages.ErrorHandler_BowerNotInstalledTitle,
				Messages.ErrorHandler_BowerNotInstalledMessage);
	}

}
