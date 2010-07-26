package zephyr.plugin.core.api.logging.helpers;

import java.lang.reflect.Array;

import zephyr.plugin.core.api.logging.abstracts.Logger;

public class ObjectArrayHandler extends PrimitiveArrayHandler {

  @Override
  public void addArray(Logger logger, Object array, String label, String id) {
    int arraySize = Array.getLength(array);
    CollectionLabelBuilder arrayLabelBuilder = new CollectionLabelBuilder(logger, arraySize, label, id);
    for (int i = 0; i < arraySize; i++) {
      logger.labelBuilder().push(arrayLabelBuilder.elementLabel(i));
      Parser.addChildObject(logger, Array.get(array, i));
      logger.labelBuilder().pop();
    }
  }

  @Override
  public boolean canHandleArray(Object array) {
    return !array.getClass().getComponentType().isPrimitive();
  }
}
