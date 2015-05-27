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

import org.eclipse.jface.preference.IPreferenceStore;
import org.jboss.tools.playground.bower.internal.Activator;
import org.jboss.tools.playground.bower.internal.BowerConstants;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class BowerPreferenceHolder {
	
	public static String getNpmLocation() {
		return getBowerPreferences().getString(BowerConstants.PREF_NPM_LOCATION);
	}

	public static void setNpmLocation(String location) {
		getBowerPreferences().setValue(BowerConstants.PREF_NPM_LOCATION, location);
	}

	private static IPreferenceStore getBowerPreferences() {
		return Activator.getDefault().getPreferenceStore();
	}

}
