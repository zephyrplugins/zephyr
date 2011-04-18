package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ArrayPrimitiveNode;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.Monitoring;


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
    ArrayPrimitiveNode arrayPrimitiveNode = new ArrayPrimitiveNode(label, parentNode, fieldValue);
    parentNode.addChild(arrayPrimitiveNode);
  }
}
