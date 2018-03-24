package zephyr.plugin.core.internal;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public class ReferenceManager {
  private final Map<CodeNode, Integer> references = new HashMap<CodeNode, Integer>();
  static private ReferenceManager manager = new ReferenceManager();

  private ReferenceManager() {
  }

  synchronized public void incRef(CodeNode codeNode) {
    Integer ref = references.get(codeNode);
    if (ref == null)
      ref = 0;
    ref++;
    references.put(codeNode, ref);
  }

  synchronized public void decRef(CodeNode codeNode) {
    Integer ref = references.get(codeNode);
    assert (ref > 0);
    ref--;
    if (ref == 0)
      references.remove(codeNode);
    else
      references.put(codeNode, ref);
  }

  public static ReferenceManager manager() {
    return manager;
  }

  synchronized public Set<CodeNode> nodes() {
    return new HashSet<CodeNode>(references.keySet());
  }
}
