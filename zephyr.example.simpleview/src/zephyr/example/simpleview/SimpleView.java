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

  private float[] data;
  private final Colors colors = new Colors();
  private SimpleModel model;
  private float maxDataValue;

  public SimpleView() {
  }

  @Override
  public boolean synchronize() {
    System.arraycopy(model.data, 0, data, 0, data.length);
    return true;
  }

  @Override
  protected void paint(GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    gc.fillRectangle(gc.getClipping());
    if (model == null)
      return;
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
  public void unset() {
    this.model = null;
    this.data = null;
  }

  @Override
  protected void set(SimpleModel model) {
    this.model = model;
    this.data = model.data.clone();
  }

  @Override
  protected Class<?> classSupported() {
    return SimpleModel.class;
  }
}
