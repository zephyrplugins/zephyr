package zephyr.plugin.tensorview.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import zephyr.plugin.core.api.monitoring.abstracts.DoubleArray;
import zephyr.plugin.core.api.monitoring.abstracts.MultiDimArray;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.Colors;
import zephyr.plugin.core.internal.views.helpers.BackgroundCanvasView;
import zephyr.plugin.plotting.internal.heatmap.ColorScale;

@SuppressWarnings("restriction")
public class RealVectorMapView extends BackgroundCanvasView<DoubleArray> {
  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(DoubleArray.class);
    }
  }

  private final Colors colors = new Colors();
  final ColorScale colorScale = new ColorScale(new RGB(255, 255, 255));
  private double[] copy;
  private int width = -1;

  @Override
  public boolean synchronize(DoubleArray vector) {
    copy = vector.accessData();
    int[] dims = null;
    if (vector instanceof MultiDimArray) {
      dims = ((MultiDimArray) vector).dim();
    }
    if (dims == null || dims.length <= 1) {
      width = -1;
      return true;
    }
    width = dims[dims.length - 1];
    return true;
  }

  @Override
  public void paint(PainterMonitor painterListener, GC gc) {
    Rectangle clipping = gc.getClipping();
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    gc.fillRectangle(clipping);
    if (copy == null) {
      return;
    }
    updateNormalizer();
    gc.setAntialias(SWT.OFF);
    double rootSize = Math.ceil(Math.sqrt(copy.length));
    double ratio = (double) clipping.width / (double) clipping.height;
    double xSize = Math.ceil(rootSize * ratio);
    double ySize = Math.ceil(rootSize / ratio);
    if (width > 0) {
      xSize = width;
      ySize = Math.ceil(copy.length / xSize);
    }
    final double xPixelSize = clipping.width / xSize;
    final double yPixelSize = clipping.height / ySize;
    final int xPixelDisplaySize = (int) Math.max(xPixelSize, 1);
    final int yPixelDisplaySize = (int) Math.max(yPixelSize, 1);
    for (int i = 0; i < copy.length; i++)
      drawWeight(gc, xSize, xPixelSize, yPixelSize, xPixelDisplaySize, yPixelDisplaySize, i, copy[i]);
  }

  protected void drawWeight(GC gc,
      double xSize,
      double xPixelSize,
      double yPixelSize,
      int xPixelDisplaySize,
      int yPixelDisplaySize,
      int index,
      double value) {
    int xCoord = (int) (index % xSize * xPixelSize);
    int yCoord = (int) ((int) (index / xSize) * yPixelSize);
    gc.setBackground(colors.color(gc, colorScale.color(value)));
    gc.fillRectangle(xCoord, yCoord, xPixelDisplaySize, yPixelDisplaySize);
  }

  protected void updateNormalizer() {
    colorScale.reset();
    for (int i = 0; i < copy.length; i++)
      colorScale.update(copy[i]);

  }

  @Override
  public void setLayout(Clock clock, DoubleArray current) {
    colorScale.init();
  }

  @Override
  public void unsetLayout() {
    copy = null;
  }

  @Override
  public void dispose() {
    super.dispose();
    colors.dispose();
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return DoubleArray.class.isInstance(instance);
  }
}
