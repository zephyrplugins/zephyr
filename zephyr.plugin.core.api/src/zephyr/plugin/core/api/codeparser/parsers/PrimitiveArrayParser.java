package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveArrayNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;


public class PrimitiveArrayParser implements FieldParser {
  @Override
  public boolean canParse(Object fieldValue) {
    Class<? extends Object> fieldClass = fieldValue.getClass();
    if (!fieldClass.isArray())
      return false;
    return CodeTrees.isPrimitive(fieldClass.getComponentType());
  }

  @Override
  public void parse(CodeParser codeParser, MutableParentNode parentNode, Field field, Object fieldValue) {
    String label = CodeTrees.labelOf(field);
    int level = CodeTrees.levelOf(field);
    int length = Array.getLength(fieldValue);
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(field, length);
    PrimitiveArrayNode arrayPrimitiveNode = new PrimitiveArrayNode(label, parentNode, fieldValue, labelBuilder, level);
    parentNode.addChild(arrayPrimitiveNode);
  }
}
