package org.jboss.tools.eclipse;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		NLS.initializeMessages(Messages.class.getPackage().getName().replace('.', '/') + "/messages", Messages.class);
	}
	
	public static String selectFolderToImport;
	public static String alreadyImportedAsProject_title;
	public static String alreadyImportedAsProject_description;
	public static String anotherProjectWithSameNameExists_title;
	public static String anotherProjectWithSameNameExists_description;
	public static String importProject;
}
