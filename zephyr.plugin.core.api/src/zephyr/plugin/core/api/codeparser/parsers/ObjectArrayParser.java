package zephyr.plugin.core.api.codeparser.parsers;

import java.lang.reflect.Array;

public class ObjectArrayParser extends AbstractCollectionParser<Object> {
  @Override
  public boolean canParse(Object fieldValue) {
    if (!fieldValue.getClass().isArray())
      return false;
    Class<?> componentType = fieldValue.getClass().getComponentType();
    return !componentType.isPrimitive();
  }

  @Override
  protected int nbChildren(Object container) {
    return Array.getLength(container);
  }

  @Override
  protected Object getElement(Object container, int index) {
    return Array.get(container, index);
  }
}
