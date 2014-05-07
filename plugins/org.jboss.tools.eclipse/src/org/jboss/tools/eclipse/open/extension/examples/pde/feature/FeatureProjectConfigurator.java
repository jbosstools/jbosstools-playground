package org.jboss.tools.eclipse.open.extension.examples.pde.feature;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.pde.internal.core.feature.Feature;
import org.eclipse.pde.internal.core.feature.FeatureFactory;
import org.eclipse.pde.internal.core.feature.FeatureImport;
import org.eclipse.pde.internal.core.feature.WorkspaceFeatureModel;
import org.eclipse.pde.internal.core.natures.PDE;
import org.eclipse.pde.internal.core.util.CoreUtility;
import org.jboss.tools.eclipse.Activator;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;

public class FeatureProjectConfigurator implements ProjectConfigurator {

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		IFile featureFile = project.getFile("feature.xml");
		if (featureFile.exists()) {
			WorkspaceFeatureModel workspaceFeatureModel = new WorkspaceFeatureModel(featureFile);
			workspaceFeatureModel.load();
			return workspaceFeatureModel.isLoaded();
		}
		return featureFile.exists();
	}

	@Override
	public IWizard getConfigurationWizard() {
		return null;
	}

	@Override
	public void applyTo(IProject project, IProgressMonitor monitor) {
		if (!PDE.hasFeatureNature(project)) {
			try {
				CoreUtility.addNatureToProject(project, PDE.FEATURE_NATURE, monitor);
			} catch (Exception ex) {
				Activator.getDefault().getLog().log(new Status(
						IStatus.ERROR,
						Activator.PLUGIN_ID,
						ex.getMessage(),
						ex));
			}
		}
	}

}
