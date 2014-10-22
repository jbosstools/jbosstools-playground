package org.jboss.tools.playground.easymport.jee.servlet;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ProjectScope;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.wst.common.project.facet.core.IFacetedProject;
import org.eclipse.wst.common.project.facet.core.IProjectFacet;
import org.eclipse.wst.common.project.facet.core.ProjectFacetsManager;
import org.jboss.tools.playground.easymport.java.JDTProjectNature;

public class DynamicProjectTools {

	public static void convertToFacetedProject(IProject project, IProgressMonitor monitor) throws Exception {
		if (!ProjectFacetsManager.isProjectFacetDefined(project.getName())) {
			new JDTProjectNature().applyTo(project, monitor);
			IFacetedProject facetedProject = ProjectFacetsManager.create(project, true, monitor);
			IProjectFacet JAVA_FACET = ProjectFacetsManager.getProjectFacet("jst.java");
	
			ProjectScope ps = new ProjectScope(project);
			IEclipsePreferences JDPprojectNode = ps.getNode(JavaCore.PLUGIN_ID);
			String compilerCompliance = JDPprojectNode.get(JavaCore.COMPILER_COMPLIANCE, "1.7");;
			if (!facetedProject.hasProjectFacet(JAVA_FACET)) {
				facetedProject.installProjectFacet(JAVA_FACET.getVersion(compilerCompliance), null, monitor);
			}
		}
	}
}
