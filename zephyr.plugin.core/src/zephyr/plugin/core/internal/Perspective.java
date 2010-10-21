package zephyr.plugin.core.internal;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {
  @Override
  public void createInitialLayout(IPageLayout layout) {
    // String editorArea = layout.getEditorArea();
    layout.setEditorAreaVisible(false);
    layout.setFixed(true);

    // layout.addStandaloneView(View.ID, false, IPageLayout.LEFT, 1.0f,
    // editorArea);
  }

}
