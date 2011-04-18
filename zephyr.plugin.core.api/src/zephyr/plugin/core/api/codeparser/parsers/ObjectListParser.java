package zephyr.plugin.core.api.codeparser.parsers;

import java.util.Collection;
import java.util.List;


public class ObjectListParser extends AbstractCollectionParser<List<?>> {
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
    return !firstElement.getClass().isPrimitive();
  }

  @Override
  protected int nbChildren(List<?> container) {
    return container.size();
  }

  @Override
  protected Object getElement(List<?> container, int index) {
    return container.get(index);
  }
}
