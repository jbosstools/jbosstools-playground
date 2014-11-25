package org.jboss.tools.playground.easymport.extension;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.IPath;

public class RecursiveFileFinder implements IResourceVisitor {

	private IFile firstFoundFile = null;
	private Set<IFile> foundFiles = new HashSet<IFile>();
	private String fileName;
	private Set<IPath> ignoredDirectories;
	
	public RecursiveFileFinder(String fileName, Set<IPath> ignoredDirectories) {
		this.fileName = fileName;
		this.ignoredDirectories = ignoredDirectories;
	}

	@Override
	public boolean visit(IResource res) {
		if (ignoredDirectories != null) {
			for (IPath ignoedDirectory : this.ignoredDirectories) {
				if (ignoedDirectory.isPrefixOf(res.getLocation())) {
					return false;
				}
			}
		}
		
		if (res.getType() == IResource.FILE && res.getName().equals(fileName)) {
			if (this.firstFoundFile == null) {
				this.firstFoundFile = (IFile)res;
			}
			this.foundFiles.add( (IFile)res );
		}
		return res instanceof IContainer;
	}
	
	/**
	 * @return the first found file with right name
	 */
	public IFile getFile() {
		return this.firstFoundFile;
	}
	
	/**
	 * @return All found files
	 */
	public Set<IFile> getFiles() {
		return this.foundFiles;
	}
	
}