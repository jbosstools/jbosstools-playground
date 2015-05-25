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
	public static final String PREF_NPM_LOCATION = "Pref_node_modules_Location"; //$NON-NLS-1$

	public static String getNodeLocation() {
		return getBowerPreferences().getString(PREF_NPM_LOCATION);
	}

	public static void setNodeLocation(String location) {
		getBowerPreferences().setValue(PREF_NPM_LOCATION, location);
	}

	public static String getBowerExecutableLocation() {
		String bowerExecutable = (PlatformUtil.isWindows()) ? BowerConstants.BOWER_CMD : BowerConstants.BOWER; // "bower.cmd" (Windows) / "bower" (Linux & Mac OS)
		File nodeModules = new File(getBowerPreferences().getString(PREF_NPM_LOCATION)); // "node_modules" dir
		if (nodeModules != null && nodeModules.exists()) {
			String bowerRoot;
			if (PlatformUtil.isWindows()) {
				// Bower Root on Windows - 'npm' folder (on the same level with "node_modules")
				bowerRoot = nodeModules.getAbsoluteFile().getParentFile().getAbsolutePath();
			} else {
				// Bower Root on Linux & Mac Os - "/node_modules/bower/bin"
				bowerRoot = nodeModules.getAbsolutePath() + File.separator + BowerConstants.BOWER + File.separator + BowerConstants.BIN;
			}
			
			File bower = new File(bowerRoot, bowerExecutable);
			if (bower != null && bower.exists()) {
				return bower.getAbsolutePath();
			}
		
		}

		return null;
	}

	private static IPreferenceStore getBowerPreferences() {
		return Activator.getDefault().getPreferenceStore();
	}

}
