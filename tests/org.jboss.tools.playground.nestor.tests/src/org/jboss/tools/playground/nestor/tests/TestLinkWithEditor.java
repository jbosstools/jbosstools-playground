package org.jboss.tools.playground.nestor.tests;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.junit.AfterClass;
import org.junit.BeforeClass;

public class TestLinkWithEditor {
	
	private static IProject parent, child;
	
	@BeforeClass
	public static void createProjects() throws Exception {
		IWorkspaceRoot wsRoot = ResourcesPlugin.getWorkspace().getRoot();
		parent = wsRoot.getProject("parent");
		parent.create(new NullProgressMonitor());
		child = wsRoot.getProject("child");
		ProjectDescription desc = new ProjectDescription();
		desc.setName("child");
		desc.setLocation(parent.getLocation().append("child"));
		child.create(desc, new NullProgressMonitor());
	}
	
	@AfterClass
	public static void deleteProjects() throws Exception {
		child.delete(true, true, new NullProgressMonitor());
		parent.delete(true, true, new NullProgressMonitor());
	}
	
	@Before
	public void enableNesting() {
		
	}
}
