package zephyr.plugin.plotting.plot2d;

import java.awt.geom.Point2D;

import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.graphics.Rectangle;

import zephyr.plugin.core.Utils;

public class Axes {
  static final private int Padding = 2;

  public class Axe {
    final double margins;

    private double minValue = Double.MAX_VALUE;
    private double maxValue = -Double.MAX_VALUE;

    double cachedMinValue, minDisplayed;
    double cachedMaxValue, maxDisplayed;

    double translation = 0.0;
    double scale = 0.0;

    protected Axe(double margins) {
      this.margins = margins;
    }

    public Axe(double translation, double scale) {
      margins = -Double.MAX_VALUE;
      this.translation = translation;
      this.scale = scale;
    }

    protected double toD(int x) {
      return x / scale - translation;
    }

    int toG(double v) {
      return (int) ((v + translation) * scale);
    }

    public void updateTransformation(int drawingLength) {
      cachedMinValue = minValue;
      minDisplayed = minValue - Math.abs(minValue * margins);
      cachedMaxValue = maxValue;
      maxDisplayed = maxValue + Math.abs(maxValue * margins);
      double length = Math.max(maxDisplayed - minDisplayed, 1e-5);
      scale = drawingLength / length;
      translation = -(minDisplayed + maxDisplayed) / 2.0;
    }

    public void update(double d) {
      assert Utils.checkValue(d);
      minValue = Math.min(minValue, d);
      maxValue = Math.max(maxValue, d);
    }

    public void reset() {
      maxValue = -Double.MAX_VALUE;
      minValue = Double.MAX_VALUE;
    }

    public boolean scalingRequired() {
      return cachedMinValue != minValue || cachedMaxValue != maxValue;
    }
  }

  public final Axe x;
  public final Axe y;
  private int drawingTranslationX = 0;
  private int drawingTranslationY = 0;
  private final Rectangle drawingZone;

  public Axes() {
    x = new Axe(0);
    y = new Axe(0.001);
    drawingZone = new Rectangle(0, 0, 0, 0);
  }

  public Axes(Axe x, Axe y, int drawingTranslationX, int drawingTranslationY, Rectangle drawingZone) {
    this.x = x;
    this.y = y;
    this.drawingTranslationX = drawingTranslationX;
    this.drawingTranslationY = drawingTranslationY;
    this.drawingZone = drawingZone;
  }

  public void updateScaling(Rectangle canvasZone) {
    updatePadding(canvasZone);
    drawingTranslationX = drawingZone.x + drawingZone.width / 2;
    drawingTranslationY = drawingZone.y + drawingZone.height / 2;
    x.updateTransformation(drawingZone.width);
    y.updateTransformation(drawingZone.height);
  }

  private void updatePadding(Rectangle canvasZone) {
    drawingZone.x = Padding;
    drawingZone.y = Padding;
    drawingZone.width = canvasZone.width - 2 * Padding;
    drawingZone.height = canvasZone.height - 2 * Padding;
  }

  public Rectangle drawingZone() {
    return drawingZone;
  }


  protected double toDX(int gx) {
    return x.toD(gx - drawingTranslationX);
  }

  protected double toDY(int gy) {
    return y.toD(-(gy - drawingTranslationY));
  }

  public int toGX(double dx) {
    return x.toG(dx) + drawingTranslationX;
  }

  public int toGY(double dy) {
    return -y.toG(dy) + drawingTranslationY;
  }

  public double scaleToDY(int gy) {
    return gy / y.scale;
  }

  public Point toG(int x, double y) {
    return new Point(toGX(x), toGY(y));
  }

  public Point2D.Double toD(Point point) {
    return new Point2D.Double(toDX(point.x), toDY(point.y));
  }

  public boolean isInDrawingZone(Point position) {
    return drawingZone.contains(position);
  }

  @Override
  protected Axes clone() {
    return new Axes(new Axe(x.translation, x.scale), new Axe(y.translation, y.scale),
                    drawingTranslationX, drawingTranslationY,
                    new Rectangle(drawingZone.x, drawingZone.y,
                                  drawingZone.width, drawingZone.height));
  }
}
