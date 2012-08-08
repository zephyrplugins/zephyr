package zephyr.plugin.plotting.internal.heatmap;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;
import zephyr.ZephyrPlotting;
import zephyr.plugin.core.internal.utils.Colors;


public class AbstractDrawer {

  public static final ColorMapDescriptor BWColorMap = new ColorMapDescriptor(new int[][] { new int[] { 0, 0, 0 },
      new int[] { 255, 255, 255 } }, new int[] { 0, 0, 255 });
  public static final ColorMapDescriptor PinkColorMap = new ColorMapDescriptor(new int[][] { new int[] { 155, 0, 0 },
      new int[] { 255, 255, 0 }, new int[] { 0, 255, 255 }, new int[] { 255, 100, 255 } }, new int[] { 0, 0, 0 });
  public static final ColorMapDescriptor DarkBlueColorMap = new ColorMapDescriptor(new int[][] {
      new int[] { 0, 0, 100 }, new int[] { 0, 255, 255 }, new int[] { 255, 255, 0 }, new int[] { 155, 0, 0 } },
                                                                                   new int[] { 0, 0, 0 });
  protected final Colors colors;
  protected ColorMap colorMap;

  public AbstractDrawer(Colors colors) {
    this.colors = colors;
    colorMap = new ColorMap(DarkBlueColorMap);
  }

  public synchronized void setColorMap(ColorMapDescriptor descriptor) {
    colorMap = new ColorMap(descriptor);
  }

  public synchronized void paintGrid(GC gc, Canvas canvas, MapData data) {
    int lineWidth = ZephyrPlotting.preferredLineWidth();
    gc.setLineWidth(lineWidth);
    Rectangle clipping = gc.getClipping();
    gc.drawLine(0, 0, 0, clipping.height);
    gc.drawLine(0, 0, clipping.width, 0);
    gc.drawLine(clipping.width, clipping.height, 0, clipping.height);
    gc.drawLine(clipping.width, clipping.height, clipping.width, 0);
    if (data == null)
      return;
    final float pixelSizeX = (float) clipping.width / data.resolutionX;
    if (pixelSizeX > lineWidth * 2)
      for (int i = 1; i < data.resolutionX; i++) {
        int x = (int) (i * pixelSizeX);
        gc.drawLine(x, 0, x, clipping.height);
      }
    final float pixelSizeY = (float) clipping.height / data.resolutionY;
    if (pixelSizeY > lineWidth * 2)
      for (int i = 1; i < data.resolutionY; i++) {
        int y = (int) (i * pixelSizeY);
        gc.drawLine(0, y, clipping.width, y);
      }
  }

  public RGB spriteColor() {
    return colorMap.spriteColor();
  }
}