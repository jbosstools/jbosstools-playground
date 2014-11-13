/*******************************************************************************
 * Copyright (c) 2014 Red Hat, Inc.
 * Distributed under license by Red Hat, Inc. All rights reserved.
 * This program is made available under the terms of the
 * Eclipse Public License v1.0 which accompanies this distribution,
 * and is available at http://www.eclipse.org/legal/epl-v10.html
 *
 * Contributor:
 *     Red Hat, Inc. - initial API and implementation
 ******************************************************************************/
package org.jboss.tools.playground.autosave.internal;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.NotEnabledException;
import org.eclipse.core.commands.NotHandledException;
import org.eclipse.core.commands.common.NotDefinedException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.jboss.tools.playground.autosave.util.CommandUtil;
import org.osgi.framework.BundleContext;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class Activator extends AbstractUIPlugin implements IStartup {

	// The plug-in ID
	public static final String PLUGIN_ID = "org.jboss.tools.playground.autosave"; //$NON-NLS-1$
	public static final String COMMAND_ID = "org.jboss.tools.playground.autosave.command"; //$NON-NLS-1$

	// The shared instance
	private static Activator plugin;

	/**
	 * The constructor
	 */
	public Activator() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
	 * )
	 */
	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}

	/**
	 * Returns the shared instance
	 *
	 * @return the shared instance
	 */
	public static Activator getDefault() {
		return plugin;
	}
	
	public static void logError(Throwable e) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, e.getMessage(), e));
	}
	
	public static void logError(Throwable e, String message) {
		getDefault().getLog().log(new Status(IStatus.ERROR, PLUGIN_ID, message, e));
	}
	
	public static void logInfo(String info) {
		getDefault().getLog().log(new Status(IStatus.INFO, PLUGIN_ID, info));
	}
	

	/**
	 * Returns an image descriptor for the image file at the given plug-in
	 * relative path
	 *
	 * @param path
	 *            the path
	 * @return the image descriptor
	 */
	public static ImageDescriptor getImageDescriptor(String path) {
		return imageDescriptorFromPlugin(PLUGIN_ID, path);
	}

	@Override
	public void earlyStartup() {
		try {
			enableAutoSaveIfNeeded();
		} catch (ExecutionException e) {
			logError(e, e.getMessage());
		} catch (NotDefinedException e) {
			logError(e, e.getMessage());
		} catch (NotEnabledException e) {
			logError(e, e.getMessage());
		} catch (NotHandledException e) {
			logError(e, e.getMessage());
		}
	}

	private void enableAutoSaveIfNeeded() throws ExecutionException, NotDefinedException, NotEnabledException, NotHandledException {
		boolean commandState = CommandUtil.getCommandState(CommandUtil.getCommand(COMMAND_ID));
		if (commandState == true) {
			AutoSaveProcessor.INSTANCE.enableAutoSave();
//			AutoSaveJob.getInstance().schedule();
		}
	}
}
