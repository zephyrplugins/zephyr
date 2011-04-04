package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.FieldHandler;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.monitoring.wrappers.Wrappers;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

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
  public void addField(DataMonitor logger, Object container, Field field, List<MonitorWrapper> wrappers, int level,
      int levelRequired) {
    Collection<Object> collection = collection(field, container);
    int arraySize = collection.size();
    CollectionLabelBuilder collectionLabelBuilder = new CollectionLabelBuilder(logger.labelBuilder(), field, arraySize);
    int index = 0;
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, wrappers);
    for (Object o : collection) {
      logger.labelBuilder().push(collectionLabelBuilder.elementLabel(index));
      Parser.addChildObject(logger, o, localWrappers, level, levelRequired);
      logger.labelBuilder().pop();
      index++;
    }
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    return collection(field, container) != null;
  }
}
