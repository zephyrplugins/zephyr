package zephyr.plugin.core.api.internal.codeparser.parsers;

import java.util.Collection;


public class ObjectCollectionParser extends AbstractCollectionParser<Collection<?>> {
  private Object[] elements;

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
  protected int nbChildren(Collection<?> container) {
    return container.size();
  }

  @Override
  protected void beginChildrenParse(Collection<?> container) {
    elements = container.toArray();
  }

  @Override
  protected void endChildrenParse() {
    elements = null;
  }


  @Override
  protected Object getElement(Collection<?> container, int index) {
    return elements[index];
  }
}
