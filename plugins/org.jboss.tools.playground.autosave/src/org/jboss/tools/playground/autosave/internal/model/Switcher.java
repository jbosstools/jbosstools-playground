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
package org.jboss.tools.playground.autosave.internal.model;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class Switcher {
	private Command on;
	private Command off;

	public Switcher(Command on, Command off) {
		this.on = on;
		this.off = off;
	}

	public void turnOn() {
		on.execute();
	}

	public void turnOff() {
		off.execute();
	}

}
