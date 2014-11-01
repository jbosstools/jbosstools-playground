package org.jboss.tools.playground.easymport;

import java.util.ArrayList;
import java.util.Collection;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class SelectConfiguratorsWizardPage extends WizardPage {

	private IProject project;
	private ArrayList<ProjectConfigurator> selectedConfigurators;

	protected SelectConfiguratorsWizardPage(IProject project, Collection<ProjectConfigurator> configurators) {
		super(SelectConfiguratorsWizard.class.getName());
		this.project = project;
		this.selectedConfigurators = new ArrayList<ProjectConfigurator>(configurators);
	}

	@Override
	public void createControl(Composite parent) {
		setTitle("Configure project '" + project.getName() + "'");
		setDescription("The following configurators can be applied on your project. Please select those you want to run:");
		
		Composite res = new Composite(parent, SWT.NONE);
		res.setLayout(new RowLayout(SWT.VERTICAL));
		
		for (final ProjectConfigurator configurator : this.selectedConfigurators) {
			final Button check = new Button(res, SWT.CHECK);
			check.setSelection(true);
			check.setText(configurator.getLabel());
			check.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					if (check.getSelection()) {
						selectedConfigurators.add(configurator);
					} else {
						selectedConfigurators.remove(configurator);
					}
				}
			});
		}
		setControl(res);
	}
	
	public Collection<ProjectConfigurator> getSelectedConfigurators() {
		return this.selectedConfigurators;
	}

}
