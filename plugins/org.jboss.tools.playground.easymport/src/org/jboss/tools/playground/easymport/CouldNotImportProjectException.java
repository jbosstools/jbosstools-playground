package org.jboss.tools.playground.easymport;

import java.io.File;

public class CouldNotImportProjectException extends Exception {

	private File location;
	
	public CouldNotImportProjectException(File location, Exception cause) {
		super("Could not import project located at " + location.getAbsolutePath(), cause);
		this.location = location;
	}
}
