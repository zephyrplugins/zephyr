package zephyr.plugin.plotting.internal.traces;

import java.util.Arrays;

import org.eclipse.ui.IMemento;

import zephyr.plugin.core.Utils;
import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;

public class PersistentTrace {
  private static final String LabelKey = "LabelKey";
  final public String[] path;
  final public String label;

  public PersistentTrace(String label, String[] path) {
    assert label != null && path != null;
    this.label = label;
    this.path = path;
  }

  @Override
  public boolean equals(Object obj) {
    if (obj == null || !(obj instanceof PersistentTrace))
      return false;
    if (super.equals(obj))
      return true;
    PersistentTrace other = (PersistentTrace) obj;
    return Arrays.deepEquals(other.path, path) && label.equals(other.label);
  }

  @Override
  public int hashCode() {
    return label.hashCode();
  }

  @Override
  public String toString() {
    return CodeTrees.mergePath(path) + label;
  }

  public static PersistentTrace load(IMemento memento) {
    String label = memento.getString(LabelKey);
    String[] path = Utils.loadPath(memento);
    if (label == null || path == null)
      return null;
    return new PersistentTrace(label, path);
  }

  public static void save(IMemento memento, String label, String[] path) {
    memento.putString(LabelKey, label);
    Utils.savePath(memento, path);
  }
}
