package zephyr.plugin.core.privates.commands;

import java.util.Map;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;

import zephyr.plugin.core.privates.synchronization.viewfinder.ViewFinder;

public class ShowNewView extends AbstractHandler {
  private static final String PARAMETER_NAME_VIEW_ID = "org.eclipse.ui.views.showView.viewId";

  public ShowNewView() {
  }

  @Override
  @SuppressWarnings("rawtypes")
  public final Object execute(final ExecutionEvent event) throws ExecutionException {
    Map parameters = event.getParameters();
    Object viewID = parameters.get(PARAMETER_NAME_VIEW_ID);
    if (viewID == null)
      return null;
    ViewFinder viewFinder = new ViewFinder((String) viewID);
    viewFinder.provideNewView();
    return null;
  }
}
