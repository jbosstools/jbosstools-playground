package org.jboss.tools.eclipse.open.extension.examples.jee.servlet;

import java.io.InputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.eclipse.core.resources.IFile;
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
import org.jboss.tools.eclipse.Messages;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class JaxRsConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		if (!new ServletProjectConfigurator().canApplyFor(project, monitor)) {
			return false;
		}
		IFile webXml = project.getFile("web.xml");
		DocumentBuilderFactory domBuilderFactory = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder domBuilder = domBuilderFactory.newDocumentBuilder();
			InputStream webXmlContent = webXml.getContents();
			Document webXmlDOM = domBuilder.parse(webXmlContent);
			webXmlContent.close();
			NodeList initParamNodes = ((Element) (((Element)webXmlDOM.getElementsByTagName("web-app").item(0)).getElementsByTagName("servlet").item(0))).getElementsByTagName("init-param");
			for (int i = 0; i < initParamNodes.getLength(); i++) {
				Element initParamElement = (Element) initParamNodes.item(i);
				NodeList paramNameList = initParamElement.getElementsByTagName("param-name");
				if (paramNameList.getLength() > 0) {
					for (int j = 0; j < paramNameList.getLength(); j++) {
						Node paramName = paramNameList.item(j);
						if (paramName.getTextContent().equals("javax.ws.rs.Application")) {
							return true;
						}
					}
				}
			}
		} catch (Exception ex) {
			return false;
		}
		return false;
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
		try {
			new ServletProjectConfigurator().applyTo(project, monitor);
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
}
