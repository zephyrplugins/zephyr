package zephyr.plugin.core.api.monitoring.abstracts;

import zephyr.plugin.core.api.codeparser.codetree.CodeNode;
import zephyr.plugin.core.api.codeparser.codetree.ParentNode;
import zephyr.plugin.core.api.codeparser.traverser.Traverser;

public class DataTraverser implements Traverser, MonitorParser {
  private final DataMonitor monitor;

  public DataTraverser(DataMonitor monitor) {
    this.monitor = monitor;
  }

  @Override
  public boolean inNode(CodeNode codeNode) {
    if (codeNode instanceof ParentNode) {
      monitor.labelBuilder().push(codeNode.label());
      return true;
    }
    if (codeNode instanceof MonitorContainer)
      ((MonitorContainer) codeNode).addToMonitor(monitor);
    return false;
  }

  @Override
  public void outNode(CodeNode codeNode) {
    if (codeNode instanceof ParentNode)
      monitor.labelBuilder().pop();
  }
}
