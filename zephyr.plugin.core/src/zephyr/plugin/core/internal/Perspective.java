package zephyr.plugin.core.internal;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import zephyr.plugin.core.internal.treeview.StructureExplorerView;
import zephyr.plugin.core.internal.views.ClocksView;

public class Perspective implements IPerspectiveFactory {
  @Override
  public void createInitialLayout(IPageLayout layout) {
    layout.setEditorAreaVisible(false);
    layout.setFixed(true);

    String editorArea = layout.getEditorArea();
    layout.addStandaloneView(StructureExplorerView.ViewID, false, IPageLayout.LEFT, 0.3f, editorArea);
    layout.addStandaloneView(ClocksView.ViewID, false, IPageLayout.BOTTOM, 0.7f, StructureExplorerView.ViewID);
  }

}
