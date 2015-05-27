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

import java.io.File;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.jboss.tools.playground.bower.internal.BowerConstants;
import org.jboss.tools.playground.bower.internal.preferences.BowerPreferenceHolder;

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

	public static boolean hasBowerJson(final IFolder folder) throws CoreException {
		IResource bowerJson = folder.findMember(BowerConstants.BOWER_JSON);
		return (bowerJson != null && bowerJson.exists());
	}

	public static boolean isBowerJson(final IResource resource) {
		return (BowerConstants.BOWER_JSON.equals(resource.getName()) && resource.exists());
	}

	public static String getNpmLocationFromPath() {
		String path = System.getenv(BowerConstants.PATH);
		String[] split = path.split(";"); //$NON-NLS-1$
		for (String p : split) {
			if (p.endsWith(BowerConstants.NPM)) {
				return p;
			}
		}
		return null;
	}
	
	public static String getBowerExecutableLocation() {
		String bowerExecutable = (PlatformUtil.isWindows()) ? BowerConstants.BOWER_CMD : BowerConstants.BOWER; // "bower.cmd" (Windows) / "bower" (Linux & Mac OS)
		File npm = new File(BowerPreferenceHolder.getNpmLocation()); // "npm" dir
		if (npm != null && npm.exists()) {
			String bowerRoot;
			if (PlatformUtil.isWindows()) {
				// Bower Root on Windows - 'npm' folder
				bowerRoot = npm.getAbsolutePath();
			} else {
				// Bower Root on Linux & Mac Os - "npm/node_modules/bower/bin"
				bowerRoot = npm.getAbsolutePath() + File.separator + BowerConstants.NODE_MODULES + File.separator
						+ BowerConstants.BOWER + File.separator + BowerConstants.BIN;
			}
			
			File bower = new File(bowerRoot, bowerExecutable);
			if (bower != null && bower.exists()) {
				return bower.getAbsolutePath();
			}
		
		}
		return null;
	}

}