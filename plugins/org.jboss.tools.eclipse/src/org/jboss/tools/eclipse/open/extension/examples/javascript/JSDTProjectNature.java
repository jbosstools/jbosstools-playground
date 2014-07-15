package org.jboss.tools.eclipse.open.extension.examples.javascript;

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
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IProjectDescription;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IClasspathEntry;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.launching.JavaRuntime;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.wst.jsdt.core.IIncludePathEntry;
import org.eclipse.wst.jsdt.core.IJavaScriptProject;
import org.eclipse.wst.jsdt.core.JavaScriptCore;
import org.jboss.tools.eclipse.Activator;
import org.jboss.tools.eclipse.Messages;
import org.jboss.tools.eclipse.open.extension.ProjectConfigurator;

public class JSDTProjectNature implements ProjectConfigurator {
	
	private final static String FILE_EXTENSION = ".js";

	private final static class JavaScriptResourceExistsFinder implements IResourceVisitor {
		private boolean hasJavaFile;
		
		@Override
		public boolean visit(IResource resource) throws CoreException {
			this.hasJavaFile = this.hasJavaFile || (resource.getType() == IResource.FILE && resource.getName().endsWith(FILE_EXTENSION));
			return !this.hasJavaFile; 
		}
		
		public boolean hasJavaFile() {
			return this.hasJavaFile;
		}
		
	}
	
	private final static class JavaScriptResourceFinder implements IResourceVisitor {
		private Set<IContainer> mostLikelySourceFolders =  new HashSet<IContainer>();

		@Override
		public boolean visit(final IResource resource) throws CoreException {
			if (resource.getType() == IResource.FILE && resource.getName().endsWith(FILE_EXTENSION)) {
				IContainer container = resource.getParent();
				IResource aParentResource = container;
				boolean anotherContainerAlreadyIncludesIt = false;
				while (!anotherContainerAlreadyIncludesIt && aParentResource.getType() != IResource.ROOT) {
					anotherContainerAlreadyIncludesIt = this.mostLikelySourceFolders.contains(aParentResource);
					aParentResource = aParentResource.getParent();
				}
				if (anotherContainerAlreadyIncludesIt) {
					return false;
				}
				this.mostLikelySourceFolders.add(container);
			} else {
				return true;
			}
			
			return false; // don't visit a file
		}
		
		public Set<IContainer> getSourceFolders() {
			return this.mostLikelySourceFolders;
		}
	}

	@Override
	public boolean canApplyFor(IProject project, IProgressMonitor monitor) {
		JavaScriptResourceExistsFinder javaResourceFinder = new JavaScriptResourceExistsFinder();
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
	public void applyTo(IProject project, IProgressMonitor monitor) {
		try {
			IProjectDescription description = project.getDescription();
			List<String> natures = Arrays.asList(description.getNatureIds());
			if (!natures.contains(JavaScriptCore.NATURE_ID)) {
				List<String> newNatures = new ArrayList<String>(natures);
				newNatures.add(JavaScriptCore.NATURE_ID);
				description.setNatureIds(newNatures.toArray(new String[newNatures.size()]));
				project.setDescription(description, monitor);
				IJavaScriptProject jsNature = JavaScriptCore.create(project);
				jsNature.open(monitor);
				List<IIncludePathEntry> includePathEntries = Arrays.asList(jsNature.getRawIncludepath());
				List<IIncludePathEntry> newIncludePathEntries = new ArrayList<IIncludePathEntry>(includePathEntries);
				// Should first check whether a JRE container is already configured
				JavaScriptResourceFinder javaResourceFinder = new JavaScriptResourceFinder();
				project.accept(javaResourceFinder);
				Set<IContainer> sourceFolders = javaResourceFinder.getSourceFolders();
				if (!sourceFolders.isEmpty()) {
					Set<IIncludePathEntry> toRemove = new HashSet<IIncludePathEntry>();
					for (IIncludePathEntry entry : newIncludePathEntries) {
						if (entry.getEntryKind() == IIncludePathEntry.CPE_SOURCE) {
							toRemove.add(entry);
						}
					}
					newIncludePathEntries.removeAll(toRemove);
					for (IContainer container : sourceFolders) {
						newIncludePathEntries.add(JavaScriptCore.newSourceEntry(container.getFullPath()));
					}
				}
				jsNature.setRawIncludepath(newIncludePathEntries.toArray(new IIncludePathEntry[newIncludePathEntries.size()]), monitor);
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
		return Messages.jsdtConfiguratorLabel;
	}

}
