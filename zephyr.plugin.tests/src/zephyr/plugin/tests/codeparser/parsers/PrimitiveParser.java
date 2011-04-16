package zephyr.plugin.tests.codeparser.parsers;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

import zephyr.plugin.tests.codeparser.codetree.ClassNode;
import zephyr.plugin.tests.codeparser.codetree.PrimitiveNode;

public class PrimitiveParser implements Parser {
  static private Class<?>[] primitives = { Double.class, Float.class, Byte.class, Boolean.class, Integer.class,
      Short.class };

  @Override
  public boolean canParse(Object fieldValue) {
    Class<? extends Object> fieldClass = fieldValue.getClass();
    if (fieldClass.isPrimitive())
      return true;
    for (Class<?> c : primitives)
      if (c.equals(fieldClass))
        return true;
    return false;
  }

  private static String buildLabel(Field parentField, Object fieldValue) {
    String parentLabel = parentField.getName();
    if (!Modifier.isFinal(parentField.getModifiers()))
      return parentLabel;
    return parentLabel + "=" + String.valueOf(fieldValue);
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
    PrimitiveNode node = new PrimitiveNode(buildLabel(field, fieldValue), parentNode, field);
    parentNode.addChild(node);
  }
}
