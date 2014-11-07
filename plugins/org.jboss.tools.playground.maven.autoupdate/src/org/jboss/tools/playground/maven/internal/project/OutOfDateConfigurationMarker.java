package org.jboss.tools.playground.maven.internal.project;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IMarkerDelta;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.m2e.core.internal.IMavenConstants;
import org.eclipse.m2e.core.internal.Messages;

public class OutOfDateConfigurationMarker implements IResourceDeltaVisitor {

  List<IProject> outOfDateProjects = new ArrayList<IProject>();
  
	@Override
	public boolean visit(IResourceDelta delta) throws CoreException {
    switch (delta.getKind()) {
       case IResourceDelta.ADDED:
       case IResourceDelta.CHANGED:
         int flags = delta.getFlags();
         if ((flags & IResourceDelta.MARKERS) != 0) {
           IResource res = delta.getResource();
           if (res == null) {
             return true;
           }
           IProject project = res.getProject();
           if (project == null || !isMavenProject(project) || outOfDateProjects.contains(project)) {
             return false;
           }
           
           IMarkerDelta[] markers = delta.getMarkerDeltas();
           boolean hasOutOfDateMarker = findOutOfDateMarkers(markers);
           if (hasOutOfDateMarker) {
            	outOfDateProjects.add(project);
           }
           return false;
         }
         break;          
       default:
    	   break;
    }
    return true; // visit the children
	}

	private boolean findOutOfDateMarkers(IMarkerDelta[] markers) throws CoreException {
	  if (markers != null && markers.length > 0) {
	    for (IMarkerDelta markerDelta : markers) {
	      IMarker marker = markerDelta.getMarker();
	      if (marker.exists()) {
	    	  String message = (String) marker.getAttribute(IMarker.MESSAGE);
	    	  if (Messages.ProjectConfigurationUpdateRequired.equals(message)) {
	    		  return true;
	    	  }
	      }
	    }
	  }
		return false;
	}
	

  private boolean isMavenProject(IProject project) throws CoreException {
    return project != null && project.isAccessible() && project.hasNature(IMavenConstants.NATURE_ID);
  }

}
