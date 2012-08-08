package zephyr.plugin.plotting.internal.heatmap;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import zephyr.plugin.core.internal.utils.Colors;

public class Function2DCanvasDrawer extends AbstractDrawer {
  static private Mask2D DefaultMask = new Mask2D() {
    @Override
    public boolean isMasked(int x, int y) {
      return false;
    }
  };

  public Function2DCanvasDrawer(Colors colors) {
    super(colors);
  }

  synchronized public void paint(GC gc, Canvas canvas, MapData data) {
    paint(gc, canvas, data, DefaultMask);
  }

  synchronized public void paint(GC gc, Canvas canvas, MapData data, Mask2D mask) {
    if (data == null) {
      gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
      gc.fillRectangle(gc.getClipping());
      return;
    }
    draw(gc, data, mask);
  }

  private void draw(GC gc, MapData data, Mask2D mask) {
    Rectangle rectangle = gc.getClipping();
    final float pixelResolutionX = (float) rectangle.width / data.resolutionX;
    final float pixelResolutionY = (float) rectangle.height / data.resolutionY;
    drawLowRes(gc, data, mask, pixelResolutionX, pixelResolutionY);
  }

  private void drawLowRes(GC gc, MapData data, Mask2D mask, final float pixelResolutionX, final float pixelResolutionY) {
    final int pixelSizeX = (int) Math.max(pixelResolutionX, 1);
    final int pixelSizeY = (int) Math.max(pixelResolutionY, 1);
    float[][] imageData = data.imageData();
    Interval rangeValue = data.rangeValue();
    final int imageDataX = imageData.length;
    for (int ax = 0; ax < imageDataX; ax++) {
      float gx = ax * pixelResolutionX;
      final int imageDataY = imageData[0].length;
      for (int ay = 0; ay < imageDataY; ay++) {
        float gy = ay * pixelResolutionY;
        if (!mask.isMasked(ax, imageDataY - ay - 1)) {
          Color color = colors.color(gc, colorMap.valueToRGB(rangeValue.scale(imageData[ax][imageDataY - ay - 1])));
          gc.setBackground(color);
          gc.fillRectangle((int) gx, (int) gy, pixelSizeX, pixelSizeY);
        }
      }
    }
  }
}
