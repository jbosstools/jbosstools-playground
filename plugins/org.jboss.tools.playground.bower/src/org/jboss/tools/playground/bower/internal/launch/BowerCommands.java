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
package org.jboss.tools.playground.bower.internal.launch;

/**
 * All Bower CLI commands 
 *
 * @see <a href="http://bower.io/docs/api/">http://bower.io/docs/api/</a>       
 * @author "Ilya Buziuk (ibuziuk)"
 */
public enum BowerCommands {
	CACHE("cache"), //$NON-NLS-1$
	HELP("help"), //$NON-NLS-1$
	HOME("home"), //$NON-NLS-1$
	INFO("info"), //$NON-NLS-1$
	INIT("init"),  //$NON-NLS-1$
	INSTALL("install"),  //$NON-NLS-1$
	LINK("link"), //$NON-NLS-1$
	LIST("list"), //$NON-NLS-1$
	LOGIN("login"), //$NON-NLS-1$
	LOOKUP("lookup"), //$NON-NLS-1$
	PRUNE("prune"), //$NON-NLS-1$
	REGISTER("register"), //$NON-NLS-1$
	SEARCH("search"), //$NON-NLS-1$
	UPDATE("update"), //$NON-NLS-1$
	UNINSTALL("uninstall"), //$NON-NLS-1$
	UNREGISTER("unregister"), //$NON-NLS-1$
	VERSION("version"); //$NON-NLS-1$
	
	private final String value;

	private BowerCommands(final String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

}
