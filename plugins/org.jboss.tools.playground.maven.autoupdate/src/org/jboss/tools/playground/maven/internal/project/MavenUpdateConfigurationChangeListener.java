package org.jboss.tools.playground.maven.internal.project;

import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.m2e.core.ui.internal.UpdateMavenProjectJob;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MavenUpdateConfigurationChangeListener implements
		IResourceChangeListener {

	private static Logger LOG = LoggerFactory.getLogger(MavenUpdateConfigurationChangeListener.class);
	
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
	  try {
    	OutOfDateConfigurationMarker visitor = new OutOfDateConfigurationMarker();
			event.getDelta().accept(visitor);
			List<IProject> outOfDateProjects = visitor.outOfDateProjects;
    	if (!outOfDateProjects.isEmpty()) {
    	  LOG.debug("Automatic update of {}", outOfDateProjects);
    	  Job updateJob = new UpdateMavenProjectJob(outOfDateProjects.toArray(new IProject[outOfDateProjects.size()]));
    	  updateJob.schedule();
    	}
  	} catch (CoreException e) {
  		e.printStackTrace();
  	}
	}
}
