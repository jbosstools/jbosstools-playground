package org.jboss.tools.playground.easymport;

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
	
	public static String eclipseProjectConfigurationLabel;
	
	public static String EasymportWizardPage_importProjectsInFolderTitle;
	public static String EasymportWizardPage_importProjectsInFolderDescription;
	public static String EasymportWizardPage_selectRootDirectory;
	public static String EasymportWizardPage_incorrectRootDirectory;
	public static String EasymportWizardPAge_browse;
	public static String EasymportWizardPage_workingSets;
	
}
