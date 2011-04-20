package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.widgets.Display;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.signals.Listener;

public class RootClassNodeListener implements Listener<ClassNode> {
  final StructureExplorer structureExplorer;

  public RootClassNodeListener(StructureExplorer structureExplorer) {
    this.structureExplorer = structureExplorer;
  }

  @Override
  public void listen(final ClassNode classNode) {
    if (structureExplorer.tree() == null)
      return;
    Display.getDefault().asyncExec(new Runnable() {
      @Override
      public void run() {
        structureExplorer.registerClassNode(classNode);
        structureExplorer.treeState().expandNodes();
      }
    });
  }
}