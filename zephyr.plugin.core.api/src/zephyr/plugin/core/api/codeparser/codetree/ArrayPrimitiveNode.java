package zephyr.plugin.core.api.codeparser.codetree;

import java.lang.reflect.Array;
import java.util.List;

import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.helpers.Loggers;
import zephyr.plugin.core.api.monitoring.wrappers.MonitorWrapper;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;


public class ArrayPrimitiveNode extends AbstractCodeNode implements MonitorContainer {
  final Object array;
  final CollectionLabelBuilder collectionLabelBuilder;

  public ArrayPrimitiveNode(String label, ParentNode parent, Object array, CollectionLabelBuilder collectionLabelBuilder) {
    super(label, parent);
    this.array = array;
    this.collectionLabelBuilder = collectionLabelBuilder;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    int length = Array.getLength(array);
    String[] elementLabels = new String[length];
    for (int i = 0; i < elementLabels.length; i++)
      elementLabels[i] = label() + collectionLabelBuilder.elementLabel(i);

    List<MonitorWrapper> wrappers = null;
    if (array.getClass().getComponentType().equals(double.class))
      Loggers.add(monitor, elementLabels, (double[]) array, wrappers);
    else if (array.getClass().getComponentType().equals(float.class))
      Loggers.add(monitor, elementLabels, (float[]) array, wrappers);
    if (array.getClass().getComponentType().equals(int.class))
      Loggers.add(monitor, elementLabels, (int[]) array, wrappers);
  }
}
