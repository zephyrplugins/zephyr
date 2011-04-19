package zephyr.plugin.core.api.codeparser.codetree;

import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public class PrimitiveCollectionNode extends AbstractCodeNode implements MonitorContainer {
  final List<?> array;
  final CollectionLabelBuilder collectionLabelBuilder;

  public PrimitiveCollectionNode(String label, ParentNode parent, List<?> array,
      CollectionLabelBuilder collectionLabelBuilder, int level) {
    super(label, parent, level);
    this.array = array;
    this.collectionLabelBuilder = collectionLabelBuilder;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    for (int i = 0; i < array.size(); i++) {
      final int elementIndex = i;
      monitor.add(path() + collectionLabelBuilder.elementLabel(i), new Monitored() {
        @Override
        public double monitoredValue(long stepTime) {
          return (Double) array.get(elementIndex);
        }
      });
    }
  }
}
