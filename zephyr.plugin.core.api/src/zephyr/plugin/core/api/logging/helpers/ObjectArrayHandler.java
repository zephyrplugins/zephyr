package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Array;
import java.util.List;

import zephyr.plugin.core.api.logging.abstracts.Logger;
import zephyr.plugin.core.api.logging.wrappers.MonitorWrapper;

public class ObjectArrayHandler extends PrimitiveArrayHandler {

  @Override
  public void addArray(Logger logger, Object array, CollectionLabelBuilder labelBuilder, List<MonitorWrapper> wrappers) {
    int arraySize = Array.getLength(array);
    for (int i = 0; i < arraySize; i++) {
      logger.labelBuilder().push(labelBuilder.elementLabel(i));
      Parser.addChildObject(logger, Array.get(array, i), wrappers);
      logger.labelBuilder().pop();
    }
  }

  @Override
  public boolean canHandleArray(Object array) {
    return !array.getClass().getComponentType().isPrimitive();
  }
}
