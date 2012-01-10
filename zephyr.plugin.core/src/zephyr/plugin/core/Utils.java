package zephyr.plugin.core;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;


public class Utils {
  private static final String PathLabelType = "nodelabel";

  static public boolean isUIThread() {
    return Display.findDisplay(Thread.currentThread()) != null;
  }

  public static <T> List<T> asList(T... ts) {
    return Arrays.asList(ts);
  }

  public static boolean checkValue(double value) {
    return !Double.isInfinite(value) && !Double.isNaN(value);
  }

  public static String[] concat(String[] array01, String... array02) {
    String[] result = new String[array01.length + array02.length];
    System.arraycopy(array01, 0, result, 0, array01.length);
    System.arraycopy(array02, 0, result, array01.length, array02.length);
    return result;
  }

  static public String[] loadPath(IMemento memento) {
    if (memento == null)
      return null;
    List<String> pathList = new ArrayList<String>();
    IMemento[] children = memento.getChildren(PathLabelType);
    if (children == null || children.length == 0)
      return null;
    for (IMemento child : children)
      pathList.add(child.getID());
    String[] loadedPath = new String[pathList.size()];
    pathList.toArray(loadedPath);
    return loadedPath;
  }

  static public void savePath(IMemento memento, String[] savedPath) {
    for (String label : savedPath)
      memento.createChild(PathLabelType, label);
  }
}
