package org.jboss.tools.playground.easymport.jee;

import org.eclipse.osgi.util.NLS;

public class Messages extends NLS {

	static {
		NLS.initializeMessages(Messages.class.getPackage().getName().replace('.', '/') + "/messages", Messages.class);
	}
	
	public static String jdtConfiguratorLabel;
	public static String mavenConfiguratorLabel;
	public static String featureConfiguratorLabel;
	public static String jpaConfiguratorLabel;
	public static String servletConfiguratorLabel;
	public static String jaxrsConfiguratorLabel;
	public static String bundleConfiguratorLabel;
	public static String jsdtConfiguratorLabel;
	public static String cordovaConfiguratorLabel;
	
}
