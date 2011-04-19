package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ArrayPrimitiveNode;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.Monitoring;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;


public class PrimitiveArrayParser implements Parser {
  @Override
  public boolean canParse(Object fieldValue) {
    Class<? extends Object> fieldClass = fieldValue.getClass();
    if (!fieldClass.isArray())
      return false;
    return fieldClass.getComponentType().isPrimitive();
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
    String label = Monitoring.labelOf(field);
    int length = Array.getLength(fieldValue);
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(field, length);
    ArrayPrimitiveNode arrayPrimitiveNode = new ArrayPrimitiveNode(label, parentNode, fieldValue, labelBuilder);
    parentNode.addChild(arrayPrimitiveNode);
  }
}
