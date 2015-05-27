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
package org.jboss.tools.playground.bower.internal;

import org.eclipse.osgi.util.NLS;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class Messages extends NLS {
	private static final String BUNDLE_NAME = Messages.class.getName().toString().toLowerCase();

	public static String BowerPreferencePage_LocationLabel;
	public static String BowerPreferencePage_NotInstalledError;
	public static String BowerPreferencePage_NotSpecifiedWarning;
	public static String BowerPreferencePage_NotValidError;
	public static String ErrorHandler_NpmNotInstalledTitle;
	public static String ErrorHandler_NpmNotInstalledMessage;
	public static String ErrorHandler_BowerNotInstalledTitle;
	public static String ErrorHandler_BowerNotInstalledMessage;
	
	static {
		NLS.initializeMessages(BUNDLE_NAME, Messages.class);
	}

	private Messages() {
	}
}
