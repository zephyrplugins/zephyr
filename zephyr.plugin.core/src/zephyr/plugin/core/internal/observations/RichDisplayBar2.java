package zephyr.plugin.core.internal.observations;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import zephyr.plugin.core.utils.Colors;

public class RichDisplayBar2 {
  private static final int MaxWidth = 15;
  private static final int VerticalMargin = 2;
  private static final int HorizontalMargin = 2;
  private static final int MeanHeight = 2;
  private static final int CurrentHeight = 3;
  private static final int SideCursorWidth = 4;
  private static final int SideCursorHeight = 6;

  private final Colors colors = new Colors();
  private final ObsStat obsStat;

  public RichDisplayBar2(ObsStat obsStat) {
    this.obsStat = obsStat;
  }

  public void paint(GC gc) {
    Rectangle clipping = gc.getClipping();
    // gc.setForeground(colors.color(gc, Colors.COLOR_BLACK));
    // gc.drawLine(clipping.x, clipping.y, clipping.width, clipping.height);
    if (clipping.width <= 2 * VerticalMargin)
      return;
    Rectangle barRectangle = computeBarRectangle(clipping);
    gc.setBackground(colors.color(gc, Colors.COLOR_LIGHT_GRAY));
    gc.fillRectangle(barRectangle);
    int meanPosition = scaledValueToYPosition(barRectangle, scale(obsStat.mean));
    drawObservedMinMax(barRectangle, gc);
    drawStandardDeviation(barRectangle, gc, meanPosition);
    drawMean(barRectangle, gc, meanPosition);
    drawDecayMinMax(barRectangle, gc);
    drawCurrentValue(barRectangle, gc);
  }

  private double scale(double value) {
    return (value - obsStat.barMin) / obsStat.length;
  }

  private void drawDecayMinMax(Rectangle barRectangle, GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_BLACK));
    drawSideCursor(barRectangle, gc, scaledValueToYPosition(barRectangle, scale(obsStat.decayMin)));
    drawSideCursor(barRectangle, gc, scaledValueToYPosition(barRectangle, scale(obsStat.decayMax)));
  }

  private void drawObservedMinMax(Rectangle barRectangle, GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_WHITE));
    double scaledMax = scale(obsStat.max);
    double scaledMin = scale(obsStat.min);
    int maxPosition = scaledValueToYPosition(barRectangle, scaledMax);
    int height = (int) ((scaledMax - scaledMin) * barRectangle.height);
    gc.fillRectangle(barRectangle.x, maxPosition, barRectangle.width, height);
  }

  private void drawCurrentValue(Rectangle barRectangle, GC gc) {
    gc.setBackground(colors.color(gc, Colors.COLOR_RED));
    int currentPosition = scaledValueToYPosition(barRectangle, scale(obsStat.current));
    gc.fillRectangle(barRectangle.x,
                     currentPosition - CurrentHeight / 2,
                     barRectangle.width, CurrentHeight);
    drawSideCursor(barRectangle, gc, currentPosition);
  }

  private void drawSideCursor(Rectangle barRectangle, GC gc, int y) {
    Point left = new Point(barRectangle.x + barRectangle.width, y);
    gc.fillPolygon(new int[] { left.x, left.y,
        left.x + SideCursorWidth, left.y - (SideCursorHeight / 2),
        left.x + SideCursorWidth, left.y + (SideCursorHeight / 2) });
  }

  private void drawMean(Rectangle barRectangle, GC gc, int meanPosition) {
    gc.setBackground(colors.color(gc, Colors.COLOR_DARK_GRAY));
    gc.fillRectangle(barRectangle.x, meanPosition - (MeanHeight / 2),
                     barRectangle.width, MeanHeight);
  }

  private int scaledValueToYPosition(Rectangle barRectangle, double scaledValue) {
    return barRectangle.y + barRectangle.height - (int) (scaledValue * barRectangle.height);
  }

  private void drawStandardDeviation(Rectangle barRectangle, GC gc, int meanPosition) {
    int stddevLength = Math.max(1, (int) (obsStat.scaledStdDev() * barRectangle.height));
    gc.setBackground(colors.color(gc, Colors.COLOR_LIGHT_RED));
    gc.fillRectangle(barRectangle.x, meanPosition - (stddevLength / 2),
                     barRectangle.width, stddevLength);
  }

  private Rectangle computeBarRectangle(Rectangle clipping) {
    int barWidth = Math.min(clipping.width - 2 * VerticalMargin - SideCursorWidth, MaxWidth);
    int verticalMargins = (clipping.width - barWidth) / 2;
    Rectangle barRectangle = new Rectangle(verticalMargins, HorizontalMargin,
                                           barWidth, clipping.height - 2 * HorizontalMargin);
    return barRectangle;
  }

  public void updateValue(double[] currentObservation) {
    obsStat.updateValue(currentObservation);
  }
}
