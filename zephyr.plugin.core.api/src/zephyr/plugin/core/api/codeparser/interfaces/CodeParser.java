package zephyr.plugin.core.api.codeparser.interfaces;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public interface CodeParser {
  void parse(MutableParentNode clockNode, Object root);

  void recursiveParseClass(ClassNode node, Object container);

  void recursiveParseInstance(MutableParentNode parentNode, Field field, Object fieldValue);

  CollectionLabelBuilder newCollectionLabelBuilder(Field field, int nbChildren);
}
