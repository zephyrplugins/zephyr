package zephyr.plugin.core.api.internal.codeparser.interfaces;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;

public interface CodeParser {
  CodeNode parse(MutableParentNode clockNode, Object root, String rootLabel);

  void recursiveParseClass(ClassNode node, Object container);

  CodeNode recursiveParseInstance(MutableParentNode parentNode, Field fieldInstance, String instanceLabel, Object instance);

  CollectionLabelBuilder newCollectionLabelBuilder(Field field, int nbChildren);
}
