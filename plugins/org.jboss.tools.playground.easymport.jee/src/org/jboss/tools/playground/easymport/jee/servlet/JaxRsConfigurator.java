package org.jboss.tools.playground.easymport.jee.servlet;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
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

public class JaxRsConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		if (!new ServletProjectConfigurator().canApplyFor(project, ignoredDirectories, monitor)) {
			return false;
		}
		RecursiveFileFinder webXMLFinder = new RecursiveFileFinder("web.xml", ignoredDirectories);
		InputStream content = null;
		BufferedReader reader = null;
		try { 
			project.accept(webXMLFinder);
			IFile webXml = webXMLFinder.getFile();
			content = webXml.getContents();
			reader = new BufferedReader(new InputStreamReader(content, webXml.getCharset()));
			boolean found = false;
			String line = null;
			while (!found && (line = reader.readLine()) != null) {
				found |= line.contains("javax.ws.rs.Application");
				found |= line.contains("javax.ws.rs.core.Application");
			}
			return found;
		} catch (Exception ex) {
			return false;
		} finally {
			try {
				reader.close();
				content.close();
			} catch (IOException ex) {
				// annoying exception handling 
			}
		}
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			new ServletProjectConfigurator().applyTo(project, ignoredDirectories, monitor);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);

			IProjectFacet JAXRS_FACET = ProjectFacetsManager.getProjectFacet("jst.jaxrs");
			if (!facetedProject.hasProjectFacet(JAXRS_FACET)) {
				IDataModel aFacetInstallDataModel = DataModelFactory.createDataModel(new WebFacetInstallDataModelProvider());
				aFacetInstallDataModel.setBooleanProperty(IJ2EEModuleFacetInstallDataModelProperties.ADD_TO_EAR, false);
				facetedProject.installProjectFacet(JAXRS_FACET.getVersion("2.0"), aFacetInstallDataModel, monitor);
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
		return Messages.jaxrsConfiguratorLabel;
	}

	@Override
	public boolean isProject(IContainer container, IProgressMonitor monitor) {
		return false; // TODO can we make sure a given directory is a jax-rs project?
	}

	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		return null;
	}
}
