package zephyr.plugin.core.api.internal.monitoring.abstracts;

import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.Traverser;

public class MonitoredDataTraverser implements Traverser, MonitorParser {
  private final DataMonitorAdapter monitorAdapter;
  private final int levelRequired;
  public static final int MonitorEverythingLevel = Integer.MAX_VALUE;

  public MonitoredDataTraverser(DataMonitorAdapter monitorAdapter, int levelRequired) {
    this.monitorAdapter = monitorAdapter;
    this.levelRequired = levelRequired;
  }

  @Override
  public boolean inNode(CodeNode codeNode) {
    if (codeNode.level() > levelRequired)
      return false;
    if (codeNode instanceof ParentNode)
      return true;
    if (codeNode instanceof MonitorContainerNode)
      monitorAdapter.add((MonitorContainerNode) codeNode);
    return false;
  }

  @Override
  public void outNode(CodeNode codeNode) {
  }
}
