package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Array;
import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;

public class ObjectArrayHandler extends PrimitiveArrayHandler {

  @Override
  public void addArray(Logger logger, Object array, String label, String id, List<MonitorWrapper> wrappers) {
    int arraySize = Array.getLength(array);
    CollectionLabelBuilder arrayLabelBuilder = new CollectionLabelBuilder(logger, arraySize, label, id);
    for (int i = 0; i < arraySize; i++) {
      logger.labelBuilder().push(arrayLabelBuilder.elementLabel(i));
      Parser.addChildObject(logger, Array.get(array, i), wrappers);
      logger.labelBuilder().pop();
    }
  }

  @Override
  public boolean canHandleArray(Object array) {
    return !array.getClass().getComponentType().isPrimitive();
  }
}
