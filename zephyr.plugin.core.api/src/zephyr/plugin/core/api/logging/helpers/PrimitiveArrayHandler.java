package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.FieldHandler;
import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.logging.wrappers.Wrappers;

public class PrimitiveArrayHandler implements FieldHandler, ArrayHandler {
  protected Object array(Field field, Object container) {
    Object array = null;
    try {
      array = field.get(container);
    } catch (IllegalArgumentException e) {
      e.printStackTrace();
    } catch (IllegalAccessException e) {
      e.printStackTrace();
    }
    return array;
  }

  @Override
  public void addField(Logger logger, Object container, Field field, List<MonitorWrapper> wrappers) {
    List<MonitorWrapper> localWrappers = Wrappers.getWrappers(field, wrappers);
    Object array = array(field, container);
    String label = Parser.labelOf(field);
    String id = Parser.idOf(field);
    boolean includeIndex = Loggers.isIndexIncluded(field);
    CollectionLabelBuilder labelBuilder = new CollectionLabelBuilder(logger, Array.getLength(array), label, id,
                                                                     includeIndex);
    addArray(logger, array, labelBuilder, Wrappers.getWrappers(field, localWrappers));
  }

  @Override
  public void addArray(Logger logger, Object array, CollectionLabelBuilder labelBuilder, List<MonitorWrapper> wrappers) {
    String[] elementLabels = new String[Array.getLength(array)];
    for (int i = 0; i < elementLabels.length; i++)
      elementLabels[i] = labelBuilder.elementLabel(i);

    if (array.getClass().getComponentType().equals(double.class))
      Loggers.add(logger, elementLabels, (double[]) array, wrappers);
    else if (array.getClass().getComponentType().equals(float.class))
      Loggers.add(logger, elementLabels, (float[]) array, wrappers);
    if (array.getClass().getComponentType().equals(int.class))
      Loggers.add(logger, elementLabels, (int[]) array, wrappers);
  }

  @Override
  public boolean canHandle(Field field, Object container) {
    if (!field.getType().isArray())
      return false;
    Object array = array(field, container);
    if (array == null)
      return false;
    return canHandleArray(array);
  }

  @Override
  public boolean canHandleArray(Object array) {
    return array.getClass().getComponentType().isPrimitive();
  }
}
