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

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.jboss.tools.playground.autosave.util.EditorUtil;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class AutoSaveJob extends Job {
	private static final String NAME = "Auto Save Job"; //$NON-NLS-1$
	private static final long DELAY = 1000;
	private static volatile AutoSaveJob instance;
	private boolean running = true;
	
	
	public static synchronized AutoSaveJob getInstance() {
		AutoSaveJob localInstance = instance;
		if (instance == null) {
			synchronized (AutoSaveJob.class) {
				localInstance = instance;
				if (localInstance == null) {
					instance = localInstance = new AutoSaveJob();
				}
			}
		}
		return localInstance;
	}
	
	private AutoSaveJob() {
		super(NAME);
	}

	@Override
	public IStatus run(IProgressMonitor monitor) {
		saveDirtyEditors();
		schedule(DELAY);
		return Status.OK_STATUS;
	}

	public boolean shouldSchedule() {
		return running;
	}

	public void stop() {
		running = false;
	}
	
	private void saveDirtyEditors() {
		EditorUtil.saveDirtyEditors();
	}

}
