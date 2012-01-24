package zephyr.plugin.plotting.heatmap;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

import zephyr.plugin.core.api.viewable.ContinuousFunction;
import zephyr.plugin.core.utils.Colors;
import zephyr.plugin.core.utils.ImageAdapter;

public class Function2DDrawer {
  static final int[][] Landmarks = new int[][] { new int[] { 155, 0, 0 }, new int[] { 255, 255, 0 },
      new int[] { 0, 255, 255 }, new int[] { 255, 100, 255 } };
  static final int[][] Diffs = colorPreprocessing();
  static final Interval[] ColorRanges = rangePreprocessing(1.0 / Diffs.length);

  private static int[] computeDiff(int[] colorA, int[] colorB) {
    return new int[] { colorA[0] - colorB[0], colorA[1] - colorB[1], colorA[2] - colorB[2] };
  }

  private static int[][] colorPreprocessing() {
    int[][] diffs = new int[Landmarks.length - 1][];
    for (int i = 0; i < diffs.length; i++)
      diffs[i] = computeDiff(Landmarks[i + 1], Landmarks[i]);
    return diffs;
  }

  private static Interval[] rangePreprocessing(double colorResolution) {
    Interval[] ranges = new Interval[Landmarks.length - 1];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Interval(i * colorResolution, (i + 1) * colorResolution);
    return ranges;
  }

  private int resolution;
  private float[][] imageData;
  private Interval rangeValue;
  private final Colors colors;
  private final ImageAdapter imageAdapter = new ImageAdapter();
  private BufferedImage bufferedImage = null;
  private boolean dirty = false;
  private Interval xRange;
  private Interval yRange;
  private ContinuousFunction function;

  public Function2DDrawer(Colors colors) {
    this.colors = colors;
  }

  synchronized public void set(Interval xRange, Interval yRange, ContinuousFunction continuousFunction, int resolution) {
    this.xRange = xRange;
    this.yRange = yRange;
    this.function = continuousFunction;
    this.resolution = resolution;
    imageData = new float[resolution][];
    for (int i = 0; i < imageData.length; i++)
      imageData[i] = new float[resolution];
    if (function != null)
      synchronize();
  }

  synchronized public void paint(GC gc, Canvas canvas) {
    if (imageData == null) {
      gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
      gc.fillRectangle(gc.getClipping());
      return;
    }
    updateBufferedImage(gc.getClipping());
    imageAdapter.paint(gc, canvas);
  }

  private void updateBufferedImage(Rectangle rectangle) {
    if (bufferedImage != null
        && (bufferedImage.getWidth() != rectangle.width || bufferedImage.getHeight() != rectangle.height))
      bufferedImage = null;
    if (bufferedImage == null) {
      bufferedImage = new BufferedImage(rectangle.width, rectangle.height, BufferedImage.TYPE_INT_ARGB);
      dirty = true;
    }
    if (!dirty)
      return;
    final float pixelSizeX = (float) rectangle.width / resolution;
    final float pixelSizeY = (float) rectangle.height / resolution;
    final int imageDataX = imageData.length;
    for (int ax = 0; ax < imageDataX; ax++) {
      float gx = ax * pixelSizeX;
      final int imageDataY = imageData[0].length;
      for (int ay = 0; ay < imageDataY; ay++) {
        float gy = ay * pixelSizeY;
        int color = valueToColor(rangeValue.scale(imageData[ax][imageDataY - ay - 1]));
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

  synchronized public void synchronize() {
    double minValue = Double.MAX_VALUE;
    double maxValue = -Double.MAX_VALUE;
    for (int ax = 0; ax < resolution; ax++) {
      double x = xRange.min + ((double) ax / resolution) * xRange.length;
      for (int ay = 0; ay < resolution; ay++) {
        double y = yRange.min + ((double) ay / resolution) * yRange.length;
        double value = function.value(new double[] { x, y });
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
        imageData[ax][ay] = (float) value;
      }
    }
    rangeValue = new Interval(minValue, maxValue);
    dirty = true;
  }


  static private int colorToInt(int r, int g, int b) {
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }

  private int valueToColor(double value) {
    double adjustedValue = Math.min(value, 1.0 - 1e-10);
    int colorIndex = (int) Math.floor(adjustedValue * (Landmarks.length - 1));
    int[] minColor = Landmarks[colorIndex];
    int[] diffColor = Diffs[colorIndex];
    Interval colorRange = ColorRanges[colorIndex];
    double scaledValue = colorRange.scale(adjustedValue);
    return colorToInt(minColor[0] + (int) (scaledValue * diffColor[0]), minColor[1]
        + (int) (scaledValue * diffColor[1]), minColor[2] + (int) (scaledValue * diffColor[2]));
  }

  synchronized public void unset() {
    imageData = null;
    bufferedImage = null;
  }
}
