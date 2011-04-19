package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Field;
import java.util.Collection;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;


public class PrimitiveListParser implements Parser {
  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof Collection))
      return false;
    Collection<?> collection = (Collection<?>) fieldValue;
    if (collection.size() == 0)
      return false;
    Object firstElement = collection.iterator().next();
    if (firstElement == null)
      return false;
    return firstElement.getClass().isPrimitive();
  }

  @Override
  public void parse(CodeParser codeParser, ClassNode parentNode, Field field, Object fieldValue) {
  }
}
