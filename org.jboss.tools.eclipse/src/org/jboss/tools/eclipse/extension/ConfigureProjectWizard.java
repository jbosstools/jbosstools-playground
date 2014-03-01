package org.jboss.tools.eclipse.extension;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;

public class ConfigureProjectWizard extends Wizard {

	private Map<ProjectConfigurator, SortedSet<IWizardPage>> pagesByConfigurator;
	
	@Override
	public void addPages() {
		this.addPage(new SelectProjectConfiguratorsWizardPage());
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
		for (Map<Entry>)
	}
	
	@Override
	public boolean performFinish() {
		
	}

}
