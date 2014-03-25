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
	
	// --- Project Natures ---
	public static String ProjectNaturesPage_label;
	public static String ProjectNaturesPage_missingNatureText;
	public static String ProjectNaturesPage_addNature;
	public static String ProjectNaturesPage_removeNature;
	public static String ProjectNaturesPage_selectNatureToAddMessage;
	public static String ProjectNaturesPage_selectNatureToAddTitle;
	public static String ProjectNaturesPage_changeWarningTitle;
	public static String ProjectNaturesPage_warningMessage;
	public static String ProjectNaturesPage_changeWarningQuestion;
}
