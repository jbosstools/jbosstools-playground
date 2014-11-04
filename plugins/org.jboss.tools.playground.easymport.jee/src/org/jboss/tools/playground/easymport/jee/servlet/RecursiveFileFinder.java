package org.jboss.tools.playground.easymport.jee.servlet;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;

class RecursiveFileFinder implements IResourceVisitor {

	private IFile foundFile = null;
	private String fileName;
	
	public RecursiveFileFinder(String fileName) {
		this.fileName = fileName;
	}

	@Override
	public boolean visit(IResource res) {
		if (res.getType() == IResource.FILE && res.getName().equals(fileName)) {
			this.foundFile = (IFile)res;
		}
		return this.foundFile == null && res instanceof IContainer;
	}
	
	public IFile getFile() {
		return this.foundFile;
	}
	
}