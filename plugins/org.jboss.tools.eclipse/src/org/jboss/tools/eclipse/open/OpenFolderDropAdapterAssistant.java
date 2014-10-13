package org.jboss.tools.eclipse.open;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspaceRoot;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.FileTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.IWorkingSet;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import org.jboss.tools.eclipse.Activator;

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
			IProject project = new OpenFolderCommand().openFolderAsProject(directory, workingSet);
			return Status.OK_STATUS;
		} catch (IOException ex) {
			return new Status(IStatus.ERROR, Activator.getDefault().getBundle().getSymbolicName(), ex.getMessage(), ex);
		}
	}

}
