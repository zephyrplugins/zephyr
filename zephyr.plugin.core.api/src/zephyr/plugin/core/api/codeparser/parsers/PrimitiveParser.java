package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.Monitoring;
import zephyr.plugin.core.api.codeparser.codetree.PrimitiveFieldNode;

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

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
    String label = Monitoring.labelOf(field);
    PrimitiveFieldNode node = new PrimitiveFieldNode(label, parentNode, field);
    parentNode.addChild(node);
  }
}
