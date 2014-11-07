/*************************************************************************************
 * Copyright (c) 2008-2014 Red Hat, Inc. and others.
 * All rights reserved. This program and the accompanying materials 
 * are made available under the terms of the Eclipse Public License v1.0
 * which accompanies this distribution, and is available at
 * http://www.eclipse.org/legal/epl-v10.html
 * 
 * Contributors:
 *     JBoss by Red Hat - Initial implementation.
 ************************************************************************************/
package org.jboss.tools.playground.maven.internal;

import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Plugin;
import org.eclipse.ui.IStartup;
import org.jboss.tools.playground.maven.internal.project.MavenUpdateConfigurationChangeListener;
import org.osgi.framework.BundleContext;

/**
 * The activator class controls the plug-in life cycle
 */
public class MavenAutoUpdateProjectActivator extends Plugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.playground.maven"; //$NON-NLS-1$

	private MavenUpdateConfigurationChangeListener mavenUpdateConfigurationChangeListener;
	  
	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext)
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		mavenUpdateConfigurationChangeListener = new MavenUpdateConfigurationChangeListener();
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				mavenUpdateConfigurationChangeListener, IResourceChangeEvent.POST_CHANGE );
	}

	/*
	 * (non-Javadoc)
	 * @see org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext)
	 */
	public void stop(BundleContext context) throws Exception {
		ResourcesPlugin.getWorkspace().removeResourceChangeListener(mavenUpdateConfigurationChangeListener);
		mavenUpdateConfigurationChangeListener = null;
		super.stop(context);
	}

	@Override
	public void earlyStartup() {
	}
}
