package org.jboss.tools.eclipse.open.extension.examples.jee.servlet;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IProject;
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
import org.jboss.tools.eclipse.Activator;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;
import org.w3c.dom.Document;

public class JPAProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		return project.getFile("persistence.xml").exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
		try {
			DynamicProjectTools.convertToFacetedProject(project, monitor);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);

			IProjectFacet JPA_FACET = ProjectFacetsManager.getProjectFacet("jpt.jpa");
			if (!facetedProject.hasProjectFacet(JPA_FACET)) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				InputStream webXmlStream = project.getFile("persistence.xml").getContents();
				Document doc = dBuilder.parse(webXmlStream);
				webXmlStream.close();
	
				IDataModel aFacetInstallDataModel = DataModelFactory.createDataModel(new WebFacetInstallDataModelProvider());
				aFacetInstallDataModel.setBooleanProperty(IJ2EEModuleFacetInstallDataModelProperties.ADD_TO_EAR, false);
				String version = doc.getElementById("persistence").getAttribute("version");
				facetedProject.installProjectFacet(JPA_FACET.getVersion(version), aFacetInstallDataModel, monitor);
			}
		} catch (Exception ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}

}
