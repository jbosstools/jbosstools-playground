package org.jboss.tools.playground.nestor.tests;

import org.eclipse.core.internal.resources.ProjectDescription;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jface.bindings.keys.KeyStroke;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swtbot.eclipse.finder.SWTBotEclipseTestCase;
import org.eclipse.swtbot.eclipse.finder.widgets.SWTBotView;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotMenu;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotToolbarDropDownButton;
import org.eclipse.swtbot.swt.finder.widgets.SWTBotTreeItem;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.intro.IIntroPart;
import org.eclipse.ui.navigator.resources.ProjectExplorer;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class TestNestedFolder extends SWTBotEclipseTestCase {

	private final class DataRetriever implements Runnable {
		private final SWTBotTreeItem item;
		Object model = null;

		private DataRetriever(SWTBotTreeItem item) {
			this.item = item;
		}

		@Override
		public void run() {
			this.model = item.widget.getData();
		}

		public Object getActualModel() {
			return this.model;
		}
	}

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
	public void openProjects() throws Exception {
		if (!parent.isOpen()) {
			parent.open(new NullProgressMonitor());
		}
		if (!child.isOpen()) {
			child.open(new NullProgressMonitor());
		}
	}

	@Before
	public void closeWelcome() throws Exception {
		IIntroPart intro = PlatformUI.getWorkbench().getIntroManager().getIntro();
		if (intro != null) {
			PlatformUI.getWorkbench().getIntroManager().closeIntro(intro);
		}
	}
	
	@Before
	public void initNewProjectNavigator() throws Exception {
		bot.viewById(ProjectExplorer.VIEW_ID).close();
		Display.getDefault().asyncExec(new Runnable() {
			@Override
			public void run() {
				try {
					IWorkbenchPage page = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getActivePage();
					IViewPart part = page.showView(ProjectExplorer.VIEW_ID);
					page.setPartState(page.getReference(part), IWorkbenchPage.STATE_MAXIMIZED);
				} catch (PartInitException ex) {
					// TODO
				}
			}
		});
	}
	
	@Test
	public void testFlat() {
		final SWTBotView viewBot = configureProjectsPresentation("Flat");
		SWTBotTreeItem[] treeItems = viewBot.bot().tree().getAllItems();
		{
			SWTBotTreeItem parentItem = null;
			for (SWTBotTreeItem item : treeItems) {
				if (item.getText().equals(parent.getName())) {
					parentItem = item;
				}
			}
			Assert.assertNotNull("Could not find parent project as root item", parentItem);
			Assert.assertEquals("Too many or missing nodes under parent", 1, parentItem.getNodes().size());
			parentItem.expand();
			SWTBotTreeItem childNode = parentItem.getNode(child.getName());
			assertModelEquals("Child item isn't the expected FOLDER", parent.getFolder("child"), childNode);
		}
		{
			SWTBotTreeItem rootChildItem = null;;
			for (SWTBotTreeItem item : treeItems) {
				if (item.getText().equals(child.getName())) {
					rootChildItem = item;
				}
			}
			Assert.assertNotNull("Found desired child project as root item", rootChildItem);
			assertModelEquals("Root node 'child' isn't the expected project", child, rootChildItem);
		}
	}
	
	@Test
	public void testNested() {
		final SWTBotView viewBot = configureProjectsPresentation("Hierarchical");
		
		SWTBotTreeItem[] treeItems = viewBot.bot().tree().getAllItems();
		{
			SWTBotTreeItem parentItem = null;
			for (SWTBotTreeItem item : treeItems) {
				if (item.getText().equals(parent.getName())) {
					parentItem = item;
				}
			}
			Assert.assertNotNull("Could not find parent project as root item", parentItem);
			Assert.assertEquals("Too many or missing nodes under parent", 1, parentItem.getNodes().size());
			parentItem.expand();
			SWTBotTreeItem childNode = parentItem.getNode(child.getName());
			assertModelEquals("Child item isn't the expected PROJECT", child, childNode);
		}
		{
			SWTBotTreeItem rootChildItem = null;;
			for (SWTBotTreeItem item : treeItems) {
				if (item.getText().equals(child.getName())) {
					rootChildItem = item;
				}
			}
			Assert.assertNull("Found undesired child project as root item", rootChildItem);
		}
	}

	/**
	 * @return
	 */
	private SWTBotView configureProjectsPresentation(String mode) {
		final SWTBotView viewBot = bot.viewById(ProjectExplorer.VIEW_ID);
		viewBot.setFocus();
		// TODO: this next line will actually fail. Cf Eclipse bugs 450872 and 451147
		SWTBotToolbarDropDownButton viewMenuButton = bot.toolbarDropDownButtonWithTooltip("View Menu");
		SWTBotMenu parentMenu = viewMenuButton.menuItem("Projects Presentation");
		final SWTBotMenu menu = parentMenu.menu(mode).click();
		// need to press ESC or parent menu remains active. Most likely a
		// bug in SWTBot when it comes to propagating close event
		parentMenu.pressShortcut(KeyStroke.getInstance(SWT.ESC));
		bot.sleep(200);
		return viewBot;
	}
	
	@Test
	public void testNestedFlatNestedFlat() {
		testNested();
		testFlat();
		testNested();
		testFlat();
	}
	
	private void assertModelEquals(String failureMessage, Object expectedModel, final SWTBotTreeItem item) {
		DataRetriever dataRetriever = new DataRetriever(item);
		item.display.syncExec(dataRetriever);
		Assert.assertEquals(failureMessage, expectedModel, dataRetriever.getActualModel());
	}
	
	@Test
	public void testNestedWithClosedProject() throws Exception {
		child.close(new NullProgressMonitor());
		testNested();
	}
	
}
