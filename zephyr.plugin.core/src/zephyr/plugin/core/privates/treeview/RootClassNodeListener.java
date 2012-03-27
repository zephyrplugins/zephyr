package zephyr.plugin.core.privates.treeview;

import zephyr.plugin.core.internal.async.events.Event;
import zephyr.plugin.core.internal.async.listeners.UIListener;
import zephyr.plugin.core.internal.events.CodeStructureEvent;

public class RootClassNodeListener extends UIListener {
  final StructureExplorerView structureExplorer;

  public RootClassNodeListener(StructureExplorerView structureExplorer) {
    this.structureExplorer = structureExplorer;
  }

  @Override
  protected void listenInUIThread(Event event) {
    if (structureExplorer.tree() == null)
      return;
    structureExplorer.registerClockChildNode(((CodeStructureEvent) event).node());
    structureExplorer.treeState().expandNodes();
  }
}