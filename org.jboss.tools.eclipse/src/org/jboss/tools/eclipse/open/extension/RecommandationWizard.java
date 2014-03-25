package org.jboss.tools.eclipse.open.extension;

import java.util.Map;
import java.util.SortedSet;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.internal.dialogs.ImportExportWizard;

public class RecommandationWizard extends ImportExportWizard {

	public RecommandationWizard(String pageId) {
		super(pageId);
	}

	private Map<ProjectConfigurator, SortedSet<IWizardPage>> pagesByConfigurator;
	
	@Override
	public void addPages() {
	}
	
	@Override
	public IWizardPage getNextPage(IWizardPage page) {
//		for (Map<Entry>)
		return null;
	}
	
	@Override
	public boolean performFinish() {
		return true;
	}

}
