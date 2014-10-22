package org.jboss.tools.playground.naturist;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		NLS.initializeMessages(Messages.class.getPackage().getName().replace('.', '/') + "/messages", Messages.class);
	}
	
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
