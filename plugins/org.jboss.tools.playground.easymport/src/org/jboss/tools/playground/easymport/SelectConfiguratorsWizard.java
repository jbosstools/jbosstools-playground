package org.jboss.tools.playground.easymport;

import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.Wizard;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class SelectConfiguratorsWizard extends Wizard {

	private IProject project;
	private Collection<ProjectConfigurator> configurators;
	private SelectConfiguratorsWizardPage page;

	public SelectConfiguratorsWizard(IProject project, Collection<ProjectConfigurator> configurators) {
		super();
		this.project = project;
		this.configurators = configurators;
	}
	
	@Override
	public void addPages() {
		this.page = new SelectConfiguratorsWizardPage(this.project, this.configurators);
		addPage(this.page);
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}
	
	public Collection<ProjectConfigurator> getSelectedConfigurators() {
		return this.page.getSelectedConfigurators();
	}

}
