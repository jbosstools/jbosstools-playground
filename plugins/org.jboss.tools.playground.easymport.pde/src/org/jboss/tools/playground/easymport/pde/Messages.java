package org.jboss.tools.playground.easymport.pde;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		NLS.initializeMessages(Messages.class.getPackage().getName().replace('.', '/') + "/messages", Messages.class);
	}
	
	public static String featureConfiguratorLabel;
	public static String bundleConfiguratorLabel;
	
}
