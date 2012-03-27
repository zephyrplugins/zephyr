package zephyr.plugin.core.utils;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IMemento;


public class Eclipse {

  static public String[] loadPath(IMemento memento) {
    if (memento == null)
      return null;
    List<String> pathList = new ArrayList<String>();
    IMemento[] children = memento.getChildren(Eclipse.PathLabelType);
    if (children == null || children.length == 0)
      return null;
    for (IMemento child : children)
      pathList.add(child.getID());
    String[] loadedPath = new String[pathList.size()];
    pathList.toArray(loadedPath);
    return loadedPath;
  }

  static public boolean isUIThread() {
    return Display.findDisplay(Thread.currentThread()) != null;
  }

  static public void savePath(IMemento memento, String[] savedPath) {
    for (String label : savedPath)
      memento.createChild(Eclipse.PathLabelType, label);
  }

  public static final String PathLabelType = "nodelabel";

}
