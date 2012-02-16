package zephyr.plugin.plotting.heatmap;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.utils.ImageAdapter;

public class Function2DDrawer {
  static final ColorMapDescriptor BWColorMap = new ColorMapDescriptor(new int[][] { new int[] { 0, 0, 0 },
      new int[] { 255, 255, 255 } }, new int[] { 0, 0, 255 });
  static final ColorMapDescriptor PinkColorMap = new ColorMapDescriptor(new int[][] { new int[] { 155, 0, 0 },
      new int[] { 255, 255, 0 }, new int[] { 0, 255, 255 }, new int[] { 255, 100, 255 } }, new int[] { 0, 0, 0 });
  private final Colors colors;
  private final ImageAdapter imageAdapter = new ImageAdapter();
  private BufferedImage bufferedImage = null;
  private boolean dirty = false;
  private ColorMap colorMap;

  public Function2DDrawer(Colors colors) {
    this.colors = colors;
    colorMap = new ColorMap(PinkColorMap);
  }

  synchronized void setColorMap(ColorMapDescriptor descriptor) {
    colorMap = new ColorMap(descriptor);
    dirty = true;
  }

  synchronized public void paint(GC gc, Canvas canvas, MapData data, boolean forceDrawing) {
    if (data == null) {
      gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
      gc.fillRectangle(gc.getClipping());
      return;
    }
    dirty = forceDrawing || dirty;
    updateBufferedImage(gc.getClipping(), data);
    imageAdapter.paint(gc, canvas);
  }

  public RGB spriteColor() {
    return colorMap.spriteColor();
  }

  private void updateBufferedImage(Rectangle rectangle, MapData data) {
    if (bufferedImage != null
        && (bufferedImage.getWidth() != rectangle.width || bufferedImage.getHeight() != rectangle.height))
      bufferedImage = null;
    if (bufferedImage == null) {
      bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
      dirty = true;
    }
    if (!dirty)
      return;
    final float pixelSizeX = (float) rectangle.width / data.resolution;
    final float pixelSizeY = (float) rectangle.height / data.resolution;
    float[][] imageData = data.imageData();
    Interval rangeValue = data.rangeValue();
    final int imageDataX = imageData.length;
    for (int ax = 0; ax < imageDataX; ax++) {
      float gx = ax * pixelSizeX;
      final int imageDataY = imageData[0].length;
      for (int ay = 0; ay < imageDataY; ay++) {
        float gy = ay * pixelSizeY;
        int color = colorMap.valueToColor(rangeValue.scale(imageData[ax][imageDataY - ay - 1]));
        updatePixel(gx, pixelSizeX, gy, pixelSizeY, color);
      }
    }
    imageAdapter.update(bufferedImage);
    dirty = false;
  }

  private void updatePixel(float gx, float pixelSizeX, float gy, float pixelSizeY, int rgb) {
    for (int dx = 0; dx < pixelSizeX; dx++) {
      final int x = (int) (gx + dx);
      for (int dy = 0; dy < pixelSizeY; dy++) {
        final int y = (int) (gy + dy);
        bufferedImage.setRGB(x, y, rgb);
      }
    }
  }

  public void unset() {
    bufferedImage = null;
  }
}
