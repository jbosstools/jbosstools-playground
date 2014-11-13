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
package org.jboss.tools.playground.autosave.util;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public final class CommandUtil {

	private CommandUtil() {
	}

	public static Boolean getCommandState(Command command) {
		if (command != null) {
			State state = command.getState(RegistryToggleState.STATE_ID);
			return (Boolean) state.getValue();
		}
		return null;
	}

	public static Command getCommand(String commandID) {
		if (commandID != null) {
			ICommandService commandService = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
			return commandService.getCommand(commandID);
		}
		return null;
	}
}
