/******************************************************************************* 
 * Copyright (c) 2015 Red Hat, Inc. 
 * Distributed under license by Red Hat, Inc. All rights reserved. 
 * This program is made available under the terms of the 
 * Eclipse Public License v1.0 which accompanies this distribution, 
 * and is available at http://www.eclipse.org/legal/epl-v10.html 
 * 
 * Contributors: 
 * Red Hat, Inc. - initial API and implementation 
 ******************************************************************************/
package org.jboss.tools.playground.bower.internal.util;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.playground.bower.internal.BowerConstants;

/**
 * @author Ilya Buziuk (ibuziuk)
 */
public final class BowerUtil {

	private BowerUtil() {
	}

	public static boolean isBowerJsonExist(final IProject project) throws CoreException {
		if (project != null) {
			IResource[] members = project.members(); 
			for (IResource member : members) {
				if (BowerConstants.BOWER_JSON.equals(member.getName()) && member.exists()) {
					return true;
				}
			}
		}
		return false;
	}
	
}