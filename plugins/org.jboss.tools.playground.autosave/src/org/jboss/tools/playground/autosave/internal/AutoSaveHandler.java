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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.handlers.HandlerUtil;
import org.jboss.tools.playground.autosave.internal.model.Switcher;
import org.jboss.tools.playground.autosave.internal.model.TurnAutoSaveOff;
import org.jboss.tools.playground.autosave.internal.model.TurnAutoSaveOn;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class AutoSaveHandler extends AbstractHandler {
	private Switcher switcher;
	
	public AutoSaveHandler() {
		org.jboss.tools.playground.autosave.internal.model.Command turnAutoSaveOn = new TurnAutoSaveOn();
		org.jboss.tools.playground.autosave.internal.model.Command turnAutoSaveOff = new TurnAutoSaveOff();
		this.switcher = new Switcher(turnAutoSaveOn, turnAutoSaveOff);
	}

	public Object execute(ExecutionEvent event) throws ExecutionException {
		Command command = event.getCommand();
		boolean newValue = !HandlerUtil.toggleCommandState(command);
		
		if (newValue) {
			switcher.turnOn();
		} else {
			switcher.turnOff();
		}
		
		return null;
	}
	
}
