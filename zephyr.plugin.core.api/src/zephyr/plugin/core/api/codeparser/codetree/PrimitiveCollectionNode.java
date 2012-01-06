package zephyr.plugin.core.api.codeparser.codetree;

import java.util.List;

import zephyr.plugin.core.api.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.monitoring.abstracts.DataMonitor;
import zephyr.plugin.core.api.monitoring.abstracts.MonitorContainer;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;
import zephyr.plugin.core.api.parsing.CollectionLabelBuilder;

public class PrimitiveCollectionNode extends AbstractCodeNode implements MonitorContainer, AbstractPrimitives {
  final List<? extends Number> array;
  final CollectionLabelBuilder collectionLabelBuilder;

  public PrimitiveCollectionNode(String label, ParentNode parent, List<? extends Number> array,
      CollectionLabelBuilder collectionLabelBuilder, int level) {
    super(label, parent, level);
    this.array = array;
    this.collectionLabelBuilder = collectionLabelBuilder;
  }

  @Override
  public void addToMonitor(DataMonitor monitor) {
    String fullLabel = CodeTrees.mergePath(path());
    for (int i = 0; i < array.size(); i++) {
      final int elementIndex = i;
      monitor.add(fullLabel + collectionLabelBuilder.elementLabel(i), level(), new Monitored() {
        @Override
        public double monitoredValue() {
          return array.get(elementIndex).doubleValue();
        }
      });
    }
  }

  @Override
  public int size() {
    return array.size();
  }
}
