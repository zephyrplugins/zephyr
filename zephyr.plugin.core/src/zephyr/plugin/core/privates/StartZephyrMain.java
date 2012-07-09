package zephyr.plugin.core.privates;

import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.internal.startup.StartupJob;

public class StartZephyrMain implements StartupJob {

  @Override
  public int level() {
    return 1000;
  }

  @Override
  public void run() {
    List<String> args = ZephyrPluginCore.getArgsFiltered();
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.runnable");
    for (IConfigurationElement element : config) {
      boolean autostart = Boolean.parseBoolean(element.getAttribute("autostart"));
      if (!autostart && !checkForID(args, element.getAttribute("id")))
        continue;
      ZephyrCore.start(element);
    }
  }

  public static RunnableFactory createRunnableFactory(final IConfigurationElement element) {
    return new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        Object o;
        try {
          o = element.createExecutableExtension("class");
          if (!(o instanceof Runnable))
            return null;
        } catch (CoreException e) {
          e.printStackTrace();
          return null;
        }
        return (Runnable) o;
      }
    };
  }

  private static boolean checkForID(List<String> args, String id) {
    for (String arg : args)
      if (id.equals(arg))
        return true;
    return false;
  }
}
