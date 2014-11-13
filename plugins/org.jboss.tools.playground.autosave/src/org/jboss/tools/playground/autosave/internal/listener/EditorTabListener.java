package org.jboss.tools.playground.autosave.internal.listener;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.ui.part.EditorPart;

/**
 * @author "Ilya Buziuk (ibuziuk)"
 */
public class EditorTabListener  implements FocusListener {

	@Override
	public void focusGained(FocusEvent e) {
	}

	@Override
	public void focusLost(FocusEvent e) {
		EditorPart editor = (EditorPart) e.getSource();
		editor.doSave(new NullProgressMonitor());
	}
	
}