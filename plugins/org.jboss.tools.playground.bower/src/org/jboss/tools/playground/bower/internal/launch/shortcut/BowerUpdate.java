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

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.playground.bower.internal.BowerConstants;
import org.jboss.tools.playground.bower.internal.launch.BowerCommands;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class BowerUpdate extends GenericBowerLaunch {
	private static final String LAUNCH_NAME = "Bower Update"; //$NON-NLS-1$

	@Override
	protected String getCommandArguments() {
		return BowerCommands.UPDATE.getValue();
	}

	@Override
	protected String getLaunchName() {
		return LAUNCH_NAME;
	}

	@Override
	protected String getWorkingDirectory(IResource resource) throws CoreException {
		if (resource != null && resource.exists()) {
			IProject project = resource.getProject();
			IResource[] members = project.members();
			for (IResource member : members) {
				if (BowerConstants.BOWER_JSON.equals(member.getName()) && member.exists()) {
					IContainer parent = member.getParent();
					if (parent != null && parent.exists()) {
						return parent.getFullPath().toOSString();
					}
				}
			}
		}
		return null;
	}

}
