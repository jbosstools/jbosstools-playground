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

import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.playground.bower.internal.Activator;
import org.jboss.tools.playground.bower.internal.BowerConstants;
import org.jboss.tools.playground.bower.internal.util.PlatformUtil;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class BowerPreferenceHolder {
	public static final String PREF_NPM_LOCATION = "Pref_npm_Location"; //$NON-NLS-1$

	public static String getNodeLocation() {
		return getBowerPreferences().getString(PREF_NPM_LOCATION);
	}

	public static void setNodeLocation(String location) {
		getBowerPreferences().setValue(PREF_NPM_LOCATION, location);
	}

	public static String getBowerExecutableLocation() {
		String bower = (PlatformUtil.isWindows()) ? BowerConstants.BOWER_CMD : BowerConstants.BOWER;
		File bowerExecutable = new File(getBowerPreferences().getString(PREF_NPM_LOCATION), bower);
		if (bowerExecutable != null && bowerExecutable.exists()) {
			return bowerExecutable.getAbsolutePath();
		}
		return null;
	}

	private static IPreferenceStore getBowerPreferences() {
		return Activator.getDefault().getPreferenceStore();
	}

}
