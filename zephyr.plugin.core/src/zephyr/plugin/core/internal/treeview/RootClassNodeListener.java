package zephyr.plugin.core.internal.treeview;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.UIListener;
import zephyr.plugin.core.events.CodeParsedEvent;

public class RootClassNodeListener extends UIListener {
  final StructureExplorerView structureExplorer;

  public RootClassNodeListener(StructureExplorerView structureExplorer) {
    this.structureExplorer = structureExplorer;
  }

  @Override
  protected void listenInUIThread(Event event) {
    if (structureExplorer.tree() == null)
      return;
    structureExplorer.registerClockChildNode(((CodeParsedEvent) event).node());
    structureExplorer.treeState().expandNodes();
  }
}