package org.jboss.tools.playground.easymport.java;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.wizard.IWizard;
import org.jboss.tools.playground.easymport.extension.ProjectConfigurator;

public class JDTProjectNature implements ProjectConfigurator {

	private final static class JavaResourceExistsFinder implements IResourceVisitor {
		private boolean hasJavaFile;
		private Set<IPath> ignoredDirectories;
		
		public JavaResourceExistsFinder(Set<IPath> ignoredDirectories) {
			this.ignoredDirectories = ignoredDirectories;
		}
		
		@Override
		public boolean visit(final IResource resource) throws CoreException {
			if (this.ignoredDirectories != null) {
				for (IPath ignoredDirectory : this.ignoredDirectories) {
					if (ignoredDirectory.isPrefixOf(resource.getLocation())) {
						return false;
					}
				}
			}
		
			this.hasJavaFile = this.hasJavaFile || (resource.getType() == IResource.FILE && resource.getName().endsWith(".java"));
			return !this.hasJavaFile; 
		}
		
		public boolean hasJavaFile() {
			return this.hasJavaFile;
		}
		
	}
	
	private final static class JavaResourceFinder implements IResourceVisitor {
		private Set<IContainer> mostLikelySourceFolders =  new HashSet<IContainer>();
		private static final String PACKAGE_KEYWORD = "package";
		private Set<IPath> ignoredDirectories;
		
		public JavaResourceFinder(Set<IPath> ignoredDirectories) {
			this.ignoredDirectories = ignoredDirectories;
		}
		
		@Override
		public boolean visit(final IResource resource) throws CoreException {
			if (this.ignoredDirectories != null) {
				for (IPath ignoredDirectory : this.ignoredDirectories) {
					if (ignoredDirectory.isPrefixOf(resource.getLocation())) {
						return false;
					}
				}
			}
			
			boolean alreadyInAResourceFolder = false;
			IResource aParentResource = resource;
			while (!alreadyInAResourceFolder && aParentResource.getType() != IResource.ROOT) {
				alreadyInAResourceFolder = this.mostLikelySourceFolders.contains(aParentResource);
				aParentResource = aParentResource.getParent();
			}
			if (alreadyInAResourceFolder || resource.getType() != IResource.FILE || !resource.getName().endsWith(".java")) {
				return !alreadyInAResourceFolder;
			}
			IFile javaFile = (IFile)resource;
			InputStream stream = javaFile.getContents();
			// TODO would be better to user AST
			BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
			String packageLine = null;
			do {
				try {
					packageLine = reader.readLine();
				} catch (IOException ex) {
					Activator.getDefault().getLog().log(new Status(
						IStatus.ERROR,
						Activator.PLUGIN_ID,
						ex.getMessage(),
						ex));
				}
			} while (packageLine != null && !packageLine.startsWith(PACKAGE_KEYWORD));
			try {
				reader.close();
				stream.close();
			} catch (IOException ex) {
				// nothing
			}
			if (packageLine != null) {
				packageLine = packageLine.substring(packageLine.indexOf(PACKAGE_KEYWORD) + PACKAGE_KEYWORD.length() + 1, packageLine.length());
				packageLine = packageLine.substring(0, packageLine.indexOf(';'));
				packageLine = packageLine.trim();
				// At this poinmt, packageLine would most likely be a package name;
				IContainer rootPackage = resource.getParent();
				if (!packageLine.isEmpty()) {
					String[] segments = packageLine.split("\\.");
					for (String segment : segments) {
						rootPackage = rootPackage.getParent();
					}
				}
				this.mostLikelySourceFolders.add(rootPackage);
			}
			return false; // don't visit a file
		}
		
		public Set<IContainer> getSourceFolders() {
			return this.mostLikelySourceFolders;
		}
	}

	@Override
	public boolean canApplyFor(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		JavaResourceExistsFinder javaResourceFinder = new JavaResourceExistsFinder(ignoredDirectories);
		try {
			project.accept(javaResourceFinder);
		} catch (CoreException ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
			return false;
		}
		return javaResourceFinder.hasJavaFile();
	}

	@Override
	public IWizard getConfigurationWizard() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void applyTo(IProject project, Set<IPath> ignoredDirectories, IProgressMonitor monitor) {
		try {
			IProjectDescription description = project.getDescription();
			List<String> natures = Arrays.asList(description.getNatureIds());
			if (!natures.contains(JavaCore.NATURE_ID)) {
				List<String> newNatures = new ArrayList<String>(natures);
				newNatures.add(JavaCore.NATURE_ID);
				description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
				project.setDescription(description, monitor);
				IJavaProject javaNature = JavaCore.create(project);
				javaNature.open(monitor);
				List<IClasspathEntry> classpathEntries = Arrays.asList(javaNature.getRawClasspath());
				List<IClasspathEntry> newClasspathEntries = new ArrayList<IClasspathEntry>(classpathEntries);
				// Should first check whether a JRE container is already configured
				newClasspathEntries.add(JavaRuntime.getDefaultJREContainerEntry());
				JavaResourceFinder javaResourceFinder = new JavaResourceFinder(ignoredDirectories);
				project.accept(javaResourceFinder);
				Set<IContainer> sourceFolders = javaResourceFinder.getSourceFolders();
				if (!sourceFolders.isEmpty()) {
					Set<IClasspathEntry> toRemove = new HashSet<IClasspathEntry>();
					for (IClasspathEntry entry : newClasspathEntries) {
						if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
							toRemove.add(entry);
						}
					}
					newClasspathEntries.removeAll(toRemove);
					for (IContainer container : sourceFolders) {
						newClasspathEntries.add(JavaCore.newSourceEntry(container.getFullPath()));
					}
				}
				javaNature.setRawClasspath(newClasspathEntries.toArray(new IClasspathEntry[newClasspathEntries.size()]), monitor);
			}
		} catch (Exception ex) {
			Activator.getDefault().getLog().log(new Status(
					IStatus.ERROR,
					Activator.PLUGIN_ID,
					ex.getMessage(),
					ex));
		}
	}
	
	@Override
	public String getLabel() {
		return Messages.jdtConfiguratorLabel;
	}
	
	@Override
	public boolean isProject(IContainer container, IProgressMonitor monitor) {
		return false; // Having a .java file isn't enough to guarantee we are at the root of a project
	}
	
	@Override
	public Set<IFolder> getDirectoriesToIgnore(IProject project, IProgressMonitor monitor) {
		Set<IFolder> res = new HashSet<IFolder>();
		try {
			IJavaProject javaProject = (IJavaProject)project.getNature(JavaCore.NATURE_ID);
			IResource resource = project.getWorkspace().getRoot().findMember(javaProject.getOutputLocation());
			if (resource != null && resource.exists() && resource.getType() == IResource.FOLDER) {
				res.add((IFolder)resource);
			}
			for (IClasspathEntry entry : javaProject.getRawClasspath()) {
				if (entry.getEntryKind() == IClasspathEntry.CPE_SOURCE) {
					IFolder sourceFolder = project.getFolder(entry.getPath());
					res.add(sourceFolder);
				}
			}
		} catch (CoreException ex) {
			Activator.log(IStatus.ERROR, ex.getMessage());
		}
		return res;
	}

}
