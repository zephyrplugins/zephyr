package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Listener;

public class RootClassNodeListener implements Listener<CodeNode> {
  final StructureExplorer structureExplorer;

  public RootClassNodeListener(StructureExplorer structureExplorer) {
    this.structureExplorer = structureExplorer;
  }

  @Override
  public void listen(final CodeNode codeNode) {
    if (structureExplorer.tree() == null)
      return;
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        structureExplorer.registerClockChildNode(codeNode);
        structureExplorer.treeState().expandNodes();
      }
    });
  }
}