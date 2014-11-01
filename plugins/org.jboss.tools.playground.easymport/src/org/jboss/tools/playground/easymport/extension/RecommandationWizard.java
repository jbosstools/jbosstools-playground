package org.jboss.tools.playground.easymport.extension;

import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.ui.internal.dialogs.ImportExportWizard;

public class RecommandationWizard extends ImportExportWizard {

	public RecommandationWizard(String pageId) {
		super(pageId);
	}

	
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
