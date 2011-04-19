package zephyr.plugin.core.api.monitoring.abstracts;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;

public class DataTraverser implements Traverser, MonitorParser {
  private final DataMonitor monitor;
  private final int levelRequired;

  public DataTraverser(DataMonitor monitor, int levelRequired) {
    this.monitor = monitor;
    this.levelRequired = levelRequired;
  }

  @Override
  public boolean inNode(CodeNode codeNode) {
    if (codeNode.level() > levelRequired)
      return false;
    if (codeNode instanceof ParentNode)
      return true;
    if (codeNode instanceof MonitorContainer)
      ((MonitorContainer) codeNode).addToMonitor(monitor);
    return false;
  }

  @Override
  public void outNode(CodeNode codeNode) {
  }
}
