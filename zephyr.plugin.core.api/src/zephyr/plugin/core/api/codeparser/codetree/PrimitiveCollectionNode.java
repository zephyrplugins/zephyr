package zephyr.plugin.core.api.codeparser.codetree;

import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public class PrimitiveCollectionNode extends AbstractCodeNode implements MonitorContainerNode, AbstractPrimitives {
  final List<? extends Number> array;
  final CollectionLabelBuilder collectionLabelBuilder;

  public PrimitiveCollectionNode(String label, ParentNode parent, List<? extends Number> array,
      CollectionLabelBuilder collectionLabelBuilder, int level) {
    super(label, parent, level);
    this.array = array;
    this.collectionLabelBuilder = collectionLabelBuilder;
  }

  @Override
  public Monitored createMonitored(String label) {
    int index = collectionLabelBuilder.indexOf(label, size());
    if (index < 0)
      return null;
    return createMonitored(index);
  }

  private Monitored createMonitored(final int index) {
    return new Monitored() {
      @Override
      public double monitoredValue() {
        return array.get(index).doubleValue();
      }
    };
  }

  @Override
  public String[] createLabels() {
    return collectionLabelBuilder.createLabels(size());
  }

  @Override
  public Monitored[] createMonitored() {
    Monitored[] result = new Monitored[size()];
    for (int i = 0; i < result.length; i++)
      result[i] = createMonitored(i);
    return result;
  }

  @Override
  public int size() {
    return array.size();
  }
}
