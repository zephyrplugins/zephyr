package zephyr.plugin.plotting.privates.traces;

import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

public class TraceExtended extends Trace {
  public final String name;
  public final Monitored monitored;

  public TraceExtended(String label, CodeNode codeNode, Monitored monitored) {
    super(label, codeNode);
    this.monitored = monitored;
    name = CodeTrees.mergePath(path()) + label;
  }

  @Override
  public String toString() {
    return name;
  }
}
