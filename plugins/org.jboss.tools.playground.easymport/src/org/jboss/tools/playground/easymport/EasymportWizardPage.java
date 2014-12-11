package org.jboss.tools.playground.easymport;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.jface.fieldassist.ControlDecoration;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.dialogs.WorkingSetConfigurationBlock;

public class EasymportWizardPage extends WizardPage {

	private File selection;
	private Set<IWorkingSet> workingSets;
	private ControlDecoration rootDirectoryTextDecorator;
	private WorkingSetConfigurationBlock workingSetsBlock;

	public EasymportWizardPage(File initialSelection, Set<IWorkingSet> initialWorkingSets) {
		super(EasymportWizard.class.getName());
		this.selection = initialSelection;
		this.workingSets = initialWorkingSets;
	}

	@Override
	public void createControl(Composite parent) {
		setTitle(Messages.EasymportWizardPage_importProjectsInFolderTitle);
		setDescription(Messages.EasymportWizardPage_importProjectsInFolderDescription);
		Composite res = new Composite(parent, SWT.NONE);
		res.setLayout(new GridLayout(3, false));
		Label rootDirectoryLabel = new Label(res, SWT.NONE);
		rootDirectoryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.CENTER, false, false));
		rootDirectoryLabel.setText(Messages.EasymportWizardPage_selectRootDirectory);
		Text rootDirectoryText = new Text(res, SWT.BORDER);
		rootDirectoryText.setLayoutData(new GridData(SWT.FILL, SWT.CENTER, true, false));
		rootDirectoryText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				EasymportWizardPage.this.selection = new File( ((Text)e.widget).getText() );
				EasymportWizardPage.this.validatePage();
			}
		});
		this.rootDirectoryTextDecorator = new ControlDecoration(rootDirectoryText, SWT.TOP | SWT.LEFT);
		this.rootDirectoryTextDecorator.setImage(getShell().getDisplay().getSystemImage(SWT.ERROR));
		this.rootDirectoryTextDecorator.setDescriptionText(Messages.EasymportWizardPage_incorrectRootDirectory);
		this.rootDirectoryTextDecorator.hide();
		Button browseButton = new Button(res, SWT.BORDER);
		browseButton.setText(Messages.EasymportWizardPAge_browse);
		browseButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				DirectoryDialog dialog = new DirectoryDialog(getShell());
				String res = dialog.open();
				if (res != null) {
					EasymportWizardPage.this.selection = new File(res);
					EasymportWizardPage.this.validatePage();
				}
			}
		});
		
		Group workingSetsGroup = new Group(res, SWT.NONE);
		workingSetsGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, false, 3, 1));
		workingSetsGroup.setLayout(new GridLayout(1, false));
		workingSetsGroup.setText(Messages.EasymportWizardPage_workingSets);
		String[] initialWS = new String[this.workingSets.size()];
		int i = 0;
		for (IWorkingSet ws : this.workingSets) {
			initialWS[i] = ws.getName();
			i++;
		}
		workingSetsBlock = new WorkingSetConfigurationBlock(initialWS, getDialogSettings());
		workingSetsBlock.createContent(workingSetsGroup);

		if (this.selection != null) {
			rootDirectoryText.setText(this.selection.getAbsolutePath());
			validatePage();
		}
		
		setControl(res);
	}

	protected void validatePage() {
		if (this.selection == null || !this.selection.isDirectory()) {
			this.rootDirectoryTextDecorator.show();
			setPageComplete(false);
		} else {
			this.rootDirectoryTextDecorator.hide();
			setPageComplete(true);
		}
	}

	public File getSelectedRootDirectory() {
		return this.selection;
	}

	public Set<IWorkingSet> getSelectedWorkingSets() {
		Set<IWorkingSet> res = new HashSet<IWorkingSet>(this.workingSetsBlock.getSelectedWorkingSets().length);
		for (IWorkingSet workingSet : this.workingSetsBlock.getSelectedWorkingSets()) {
			res.add(workingSet);
		}
		return res;
	}

}
