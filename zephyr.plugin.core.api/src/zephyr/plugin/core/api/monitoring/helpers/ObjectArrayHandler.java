package zephyr.plugin.core.api.monitoring.helpers;

import java.lang.reflect.Array;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public class ObjectArrayHandler extends PrimitiveArrayHandler {

  @Override
  public void addArray(DataMonitor logger, Object array, CollectionLabelBuilder labelBuilder,
      List<MonitorWrapper> wrappers,
      int level, int levelRequired) {
    int arraySize = Array.getLength(array);
    for (int i = 0; i < arraySize; i++) {
      logger.labelBuilder().push(labelBuilder.elementLabel(i));
      Parser.addChildObject(logger, Array.get(array, i), wrappers, level, levelRequired);
      logger.labelBuilder().pop();
    }
  }

  @Override
  public boolean canHandleArray(Object array) {
    return !array.getClass().getComponentType().isPrimitive();
  }
}
