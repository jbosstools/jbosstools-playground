package org.jboss.tools.playground.easymport.jee.servlet;

import java.io.InputStream;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jst.j2ee.project.facet.IJ2EEModuleFacetInstallDataModelProperties;
import org.eclipse.jst.j2ee.web.project.facet.WebFacetInstallDataModelProvider;
import org.eclipse.wst.common.frameworks.datamodel.DataModelFactory;
import org.eclipse.wst.common.frameworks.datamodel.IDataModel;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;
import org.jboss.tools.playground.easymport.extension.RecursiveFileFinder;
import org.jboss.tools.playground.easymport.jee.Activator;
import org.jboss.tools.playground.easymport.jee.Messages;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class ServletProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			RecursiveFileFinder finder = new RecursiveFileFinder("web.xml", ignoredDirectories);
			project.accept(finder);
			return finder.getFile() != null;
		} catch (CoreException ex) {
			return false;
		}
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			DynamicProjectTools.convertToFacetedProject(project, ignoredDirectories, monitor);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);

			IProjectFacet WEB_FACET = ProjectFacetsManager.getProjectFacet("jst.web");
			if (!facetedProject.hasProjectFacet(WEB_FACET)) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				RecursiveFileFinder finder = new RecursiveFileFinder("web.xml", ignoredDirectories);
				project.accept(finder);
				InputStream webXmlStream = finder.getFile().getContents();
				Document doc = dBuilder.parse(webXmlStream);
				webXmlStream.close();
	
				IDataModel aFacetInstallDataModel = DataModelFactory.createDataModel(new WebFacetInstallDataModelProvider());
				aFacetInstallDataModel.setBooleanProperty(IJ2EEModuleFacetInstallDataModelProperties.ADD_TO_EAR, false);
				String version = ((Element)doc.getElementsByTagName("web-app").item(0)).getAttribute("version");
				if (version.isEmpty()) {
					// TODO decide this according to JRE version : Java6 => servlet 2.5; Java 7 => servlet 3.1
					version = "2.5";
				}
				facetedProject.installProjectFacet(WEB_FACET.getVersion(version), aFacetInstallDataModel, monitor);
			}
		} catch (Exception ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}
	
	@Override
	public String getLabel() {
		return Messages.servletConfiguratorLabel;
	}

	@Override
	public boolean isProject(IContainer container, IProgressMonitor monitor) {
		return false; // TODO: can we make sure a dir is a JEE project?
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}
}
