package zephyr.plugin.core.api.internal.codeparser.codetree;

import java.lang.reflect.Array;

import zephyr.plugin.core.api.internal.codeparser.interfaces.ParentNode;
import zephyr.plugin.core.api.internal.monitoring.abstracts.MonitorContainerNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;
import zephyr.plugin.core.api.monitoring.abstracts.Monitored;


public abstract class PrimitiveArrayNode<T> extends AbstractCodeNode implements MonitorContainerNode,
    AbstractPrimitives {
  static class PrimitiveDoubleArrayNode extends PrimitiveArrayNode<double[]> {
    protected PrimitiveDoubleArrayNode(String label, ParentNode parent, int level,
        CollectionLabelBuilder collectionLabelBuilder, double[] array) {
      super(label, parent, level, collectionLabelBuilder, array);
    }

    @Override
    protected Monitored createMonitored(final int i) {
      return new Monitored() {
        @Override
        public double monitoredValue() {
          return array[i];
        }
      };
    }
  }

  static class PrimitiveIntegerArrayNode extends PrimitiveArrayNode<int[]> {
    protected PrimitiveIntegerArrayNode(String label, ParentNode parent, int level,
        CollectionLabelBuilder collectionLabelBuilder, int[] array) {
      super(label, parent, level, collectionLabelBuilder, array);
    }

    @Override
    protected Monitored createMonitored(final int i) {
      return new Monitored() {
        @Override
        public double monitoredValue() {
          return array[i];
        }
      };
    }
  }

  static class PrimitiveFloatArrayNode extends PrimitiveArrayNode<float[]> {
    protected PrimitiveFloatArrayNode(String label, ParentNode parent, int level,
        CollectionLabelBuilder collectionLabelBuilder, float[] array) {
      super(label, parent, level, collectionLabelBuilder, array);
    }

    @Override
    protected Monitored createMonitored(final int i) {
      return new Monitored() {
        @Override
        public double monitoredValue() {
          return array[i];
        }
      };
    }
  }

  final protected CollectionLabelBuilder collectionLabelBuilder;
  final protected T array;

  protected PrimitiveArrayNode(String label, ParentNode parent, int level,
      CollectionLabelBuilder collectionLabelBuilder, T array) {
    super(label, parent, level);
    this.collectionLabelBuilder = collectionLabelBuilder;
    this.array = array;
  }

  @Override
  public Monitored createMonitored(String label) {
    int index = collectionLabelBuilder.indexOf(label, size());
    if (index < 0)
      return null;
    return createMonitored(index);
  }

  @Override
  public String[] createLabels() {
    return collectionLabelBuilder.createLabels(size());
  }

  @Override
  public Monitored[] createMonitored() {
    Monitored[] monitored = new Monitored[size()];
    for (int i = 0; i < monitored.length; i++)
      monitored[i] = createMonitored(i);
    return monitored;
  }

  abstract protected Monitored createMonitored(int i);

  @Override
  public int size() {
    return Array.getLength(array);
  }

  static public PrimitiveArrayNode<?> createPrimitiveArrayNode(String label, ParentNode parent, Object array,
      CollectionLabelBuilder collectionLabelBuilder, int level) {
    if (array.getClass().getComponentType().equals(double.class))
      return new PrimitiveDoubleArrayNode(label, parent, level, collectionLabelBuilder, (double[]) array);
    else if (array.getClass().getComponentType().equals(float.class))
      return new PrimitiveFloatArrayNode(label, parent, level, collectionLabelBuilder, (float[]) array);
    if (array.getClass().getComponentType().equals(int.class))
      return new PrimitiveIntegerArrayNode(label, parent, level, collectionLabelBuilder, (int[]) array);
    return null;
  }
}
