package zephyr.example.simpleview;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;


public class SimpleView extends ForegroundCanvasView<SimpleModel> {
  static private int CircleSize = 10;

  public static class Provider extends ClassViewProvider {
    public Provider() {
      super(SimpleModel.class);
    }
  }

  private float[] data = new float[0];
  private final Colors colors = new Colors();
  private float maxDataValue;

  public SimpleView() {
  }

  @Override
  public boolean synchronize() {
    if (data.length != instance().data.length)
      data = instance().data.clone();
    else
      System.arraycopy(instance().data, 0, data, 0, data.length);
    return true;
  }

  @Override
  protected void paint(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
    updateMaxData();
    float scaleX = gc.getClipping().width / data.length;
    float scaleY = ((gc.getClipping().height - CircleSize * 2) / 2.0f) / maxDataValue;
    for (int i = 0; i < data.length; i++)
      gc.drawOval((int) (i * scaleX), (int) (data[i] * scaleY + (gc.getClipping().height / 2.0)), CircleSize,
                  CircleSize);
  }

  private void updateMaxData() {
    for (float value : data)
      maxDataValue = Math.max(maxDataValue, Math.abs(value));
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return SimpleModel.class.isInstance(instance);
  }
}
