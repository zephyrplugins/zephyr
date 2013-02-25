package zephyr.plugin.core.internal.helpers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;

public interface CodeNodeToInstance<T> {
  public class Default<T> implements CodeNodeToInstance<T> {
    @Override
    public T toInstance(ClassNode codeNode) {
      return (T) codeNode.instance();
    }
  }

  T toInstance(ClassNode codeNode);
}