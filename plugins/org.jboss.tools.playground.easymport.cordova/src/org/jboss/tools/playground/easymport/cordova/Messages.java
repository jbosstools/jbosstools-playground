package org.jboss.tools.playground.easymport.cordova;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		NLS.initializeMessages(Messages.class.getPackage().getName().replace('.', '/') + "/messages", Messages.class);
	}
	
	public static String cordovaConfiguratorLabel;
	
}
