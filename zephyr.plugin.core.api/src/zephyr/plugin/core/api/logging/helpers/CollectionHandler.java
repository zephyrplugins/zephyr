package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Field;
import java.util.Collection;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logger;

public class CollectionHandler implements FieldHandler {
  @SuppressWarnings("unchecked")
  protected Collection<Object> collection(Field field, Object container) {
    Collection<Object> collection = null;
    try {
      collection = (Collection<Object>) field.get(container);
    } catch (ClassCastException e) {
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return collection;
  }

  @Override
  public void addField(Logger logger, Object container, Field field) {
    Collection<Object> collection = collection(field, container);
    int arraySize = collection.size();
    CollectionLabelBuilder collectionLabelBuilder = new CollectionLabelBuilder(logger, field, arraySize);
    int index = 0;
    for (Object o : collection) {
      logger.labelBuilder().push(collectionLabelBuilder.elementLabel(index));
      Parser.addChildObject(logger, o);
      logger.labelBuilder().pop();
      index++;
    }
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    return collection(field, container) != null;
  }
}
