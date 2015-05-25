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
package org.jboss.tools.playground.bower.internal.launch.shortcut;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.playground.bower.internal.launch.BowerCommands;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class BowerInit extends GenericBowerLaunch {
	private static final String LAUNCH_NAME = "Bower Init"; //$NON-NLS-1$
	
	@Override
	protected String getCommandArguments() {
		return BowerCommands.INIT.getValue() + " --config.interactive";  //$NON-NLS-1$
	}

	@Override
	protected String getLaunchName() {
		return LAUNCH_NAME;
	}

	@Override
	protected String getWorkingDirectory(IResource resource) throws CoreException {
		if (resource != null && resource.exists()) {
			return resource.getFullPath().toOSString();
		}
		return null;
	}
}