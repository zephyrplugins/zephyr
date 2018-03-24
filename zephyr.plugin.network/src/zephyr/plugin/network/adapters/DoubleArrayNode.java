package zephyr.plugin.network.adapters;

import zephyr.plugin.core.api.internal.codeparser.codetree.PrimitiveArrayNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;

@SuppressWarnings("restriction")
public class DoubleArrayNode extends PrimitiveArrayNode<double[]> {
  final IVectorAdapter vectorAdapter;

  public DoubleArrayNode(String label, ParentNode parent, int level, CollectionLabelBuilder collectionLabelBuilder,
      IVectorAdapter vectorAdapter) {
    super(label, parent, level, collectionLabelBuilder, vectorAdapter.size());
    this.vectorAdapter = vectorAdapter;
  }

  @Override
  protected Monitored createMonitored(final int i) {
    return new Monitored() {
      @Override
      public double monitoredValue() {
        return vectorAdapter.getEntry(i);
      }
    };
  }
}
