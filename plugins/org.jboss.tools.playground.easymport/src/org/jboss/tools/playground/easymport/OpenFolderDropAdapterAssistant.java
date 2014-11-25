package org.jboss.tools.playground.easymport;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;

public class OpenFolderDropAdapterAssistant extends CommonDropAdapterAssistant {

	public OpenFolderDropAdapterAssistant() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean isSupportedType(TransferData aTransferType) {
		return FileTransfer.getInstance().isSupportedType(aTransferType);
	}
	
	@Override
	public IStatus validateDrop(Object target, int operation, TransferData transferType) {
		if (target instanceof IWorkingSet || target instanceof IWorkspaceRoot) {
			return Status.OK_STATUS;
		} else if (target instanceof IAdaptable) {
			IAdaptable targetAdaptable = (IAdaptable)target;
			if (targetAdaptable.getAdapter(IWorkspaceRoot.class) != null || targetAdaptable.getAdapter(IWorkingSet.class) != null) {
				return Status.OK_STATUS;
			}
		}
		return Status.CANCEL_STATUS;
	}

	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
		try {
			String[] files = (String[]) aDropTargetEvent.data;
			// Currently only support single directory
			if (files.length != 1) {
				return Status.CANCEL_STATUS;
			}
			File directory = new File(files[0]);
			if (!directory.isDirectory()) {
				return Status.CANCEL_STATUS;
			}
			IWorkingSet workingSet = null;
			if (aTarget != null) {
				if (aTarget instanceof IWorkingSet) {
					workingSet = (IWorkingSet)aTarget;
				} else if (aTarget instanceof IAdaptable) {
					workingSet = (IWorkingSet) ((IAdaptable)aTarget).getAdapter(IWorkingSet.class);
				}
			}
			Set<IWorkingSet> workingSets = new HashSet<IWorkingSet>();
			workingSets.add(workingSet);
			IProgressMonitor progressMonitor = new NullProgressMonitor();
			OpenFolderCommand openFolder = new OpenFolderCommand();
			IProject project = openFolder.toExistingOrNewProject(directory, workingSets, progressMonitor);
			openFolder.importProjectAndChildrenRecursively(project, true, workingSets, progressMonitor);
			return Status.OK_STATUS;
		} catch (Exception ex) {
			return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), ex.getMessage(), ex);
		}
	}

}
