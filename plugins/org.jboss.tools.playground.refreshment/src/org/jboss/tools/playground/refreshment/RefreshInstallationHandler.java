package org.jboss.tools.playground.refreshment;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.equinox.internal.provisional.configurator.Configurator;
import org.eclipse.ui.PlatformUI;

public class RefreshInstallationHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Configurator configurator = PlatformUI.getWorkbench().getService(Configurator.class);
		try {
			configurator.applyConfiguration();
		} catch (IOException ex) {
			throw new ExecutionException(ex.getMessage(), ex);
		}
		return null;
	}

}
