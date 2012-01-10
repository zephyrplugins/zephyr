package zephyr.plugin.plotting.internal.traces;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;

public class Trace {
  final public CodeNode codeNode;
  final public String label;

  public Trace(String label, CodeNode codeNode) {
    this.label = label;
    this.codeNode = codeNode;
  }

  public String[] path() {
    return codeNode.path();
  }

  public Clock clock() {
    return CodeTrees.clockOf(codeNode);
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof Trace))
      return false;
    if (super.equals(obj))
      return true;
    Trace other = (Trace) obj;
    return other.codeNode == codeNode && label.equals(other.label);
  }

  @Override
  public int hashCode() {
    return codeNode.hashCode() + label.hashCode();
  }
}
