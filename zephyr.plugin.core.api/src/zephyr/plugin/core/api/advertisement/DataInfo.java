package zephyr.plugin.core.api.advertisement;

import java.util.ArrayList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import zephyr.plugin.core.api.labels.Labeled;

public class DataInfo implements Labeled {
  private final String fullLabel;
  private final String shortLabel;
  private final List<Object> parents;
  private final Object objectInfo;

  public DataInfo(String shortLabel, String fullLabel, Stack<Object> parents, Object info) {
    this.fullLabel = fullLabel;
    this.shortLabel = shortLabel;
    this.parents = new ArrayList<Object>(parents);
    objectInfo = info;
  }

  @Override
  public String label() {
    return fullLabel;
  }

  public List<Object> parents() {
    return parents;
  }

  public Object objectInfo() {
    return objectInfo;
  }

  public String shortLabel() {
    return shortLabel;
  }

  public String fullLabel() {
    return fullLabel;
  }

  public Object findParent(Class<?> classType) {
    ListIterator<Object> iter = parents.listIterator(parents.size());
    while (iter.hasPrevious()) {
      Object o = iter.previous();
      if (classType.isInstance(o))
        return o;
    }
    return null;
  }
}
