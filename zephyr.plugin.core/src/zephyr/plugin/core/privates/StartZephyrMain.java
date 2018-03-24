package zephyr.plugin.core.privates;

import java.util.Arrays;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.Platform;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.core.api.ParameterizedRunnable;
import zephyr.plugin.core.internal.startup.StartupJob;

public class StartZephyrMain implements StartupJob {
  @Override
  public int level() {
    return 1000;
  }

  @Override
  public void run() {
    startAutostartElements();
    startRunnableInArgs();
  }

  static private void startRunnableInArgs() {
    List<String> args = ZephyrPluginCore.getArgsFiltered();
    for (String arg : args) {
      String[] array = arg.split(",");
      String id = array[0];
      String[] parameters = Arrays.copyOfRange(array, 1, array.length);
      RunnableFactory runnable = ZephyrCore.findRunnable(id, parameters);
      if (runnable == null)
        continue;
      ZephyrCore.start(runnable);
    }
  }

  static private void startAutostartElements() {
    IConfigurationElement[] config = Platform.getExtensionRegistry().getConfigurationElementsFor("zephyr.runnable");
    for (IConfigurationElement element : config) {
      boolean autostart = Boolean.parseBoolean(element.getAttribute("autostart"));
      if (!autostart)
        continue;
      ZephyrCore.start(createRunnableFactory(element, new String[] {}));
    }
  }

  public static RunnableFactory createRunnableFactory(final IConfigurationElement element, final String[] parameters) {
    return new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        Object o;
        try {
          o = element.createExecutableExtension("class");
          if (!(o instanceof Runnable))
            return null;
          if (o instanceof ParameterizedRunnable)
            ((ParameterizedRunnable) o).setParameters(parameters);
        } catch (CoreException e) {
          e.printStackTrace();
          return null;
        }
        return (Runnable) o;
      }
    };
  }
}
