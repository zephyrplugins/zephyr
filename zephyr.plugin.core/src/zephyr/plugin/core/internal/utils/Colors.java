package zephyr.plugin.core.internal.utils;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.RGB;

public class Colors {
  static final public RGB COLOR_BLACK = new RGB(0, 0, 0);
  static final public RGB COLOR_WHITE = new RGB(255, 255, 255);
  static final public RGB COLOR_RED = new RGB(255, 0, 0);
  static final public RGB COLOR_GREEN = new RGB(0, 255, 0);
  static final public RGB COLOR_BLUE = new RGB(0, 0, 255);
  static final public RGB COLOR_DARK_RED = new RGB(128, 0, 0);
  static final public RGB COLOR_DARK_GREEN = new RGB(0, 128, 0);
  static final public RGB COLOR_DARK_BLUE = new RGB(0, 0, 128);
  static final public RGB COLOR_YELLOW = new RGB(255, 255, 0);
  static final public RGB COLOR_MAGENTA = new RGB(255, 0, 255);
  static final public RGB COLOR_CYAN = new RGB(0, 255, 255);
  static final public RGB COLOR_DARK_YELLOW = new RGB(128, 128, 0);
  static final public RGB COLOR_DARK_MAGENTA = new RGB(128, 0, 128);
  static final public RGB COLOR_DARK_CYAN = new RGB(0, 128, 128);
  public static final RGB COLOR_GRAY = new RGB(128, 128, 128);
  public static final RGB COLOR_DARK_GRAY = new RGB(64, 64, 64);
  public static final RGB COLOR_LIGHT_GRAY = new RGB(192, 192, 192);
  public static final RGB COLOR_LIGHT_RED = new RGB(255, 192, 192);
  public static final RGB COLOR_LIGHT_GREEN = new RGB(192, 255, 192);
  public static final RGB COLOR_LIGHT_BLUE = new RGB(192, 192, 255);

  final protected Map<RGB, Color> colors = new LinkedHashMap<RGB, Color>();

  public Color color(GC gc, RGB rgb) {
    Color color = colors.get(rgb);
    if (color == null) {
      color = new Color(gc.getDevice(), rgb);
      colors.put(rgb, color);
      if (colors.size() > 1000) {
        Iterator<Entry<RGB, Color>> iterator = colors.entrySet().iterator();
        iterator.next();
        iterator.remove();
      }
    }
    return color;
  }

  public void dispose() {
    for (Color color : colors.values())
      color.dispose();
    colors.clear();
  }

  public static int colorToInt(int r, int g, int b) {
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }
}
