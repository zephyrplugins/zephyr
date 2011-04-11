package zephyr.plugin.core.utils;

import java.io.IOException;

import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.Bundle;

public class Helper {
  static public String getPluginLocation(String bundleID, String pluginRelativePath) {
    return getPluginLocation(Platform.getBundle(bundleID), pluginRelativePath);
  }

  static public String getPluginLocation(Bundle bundle, String pluginRelativePath) {
    try {
      return FileLocator.toFileURL(FileLocator.find(bundle, new Path(pluginRelativePath), null)).getFile();
    } catch (IOException ex) {
      return "";
    }
  }

  static public boolean booleanState(String id, boolean defaultValue) {
    ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
    if (service == null)
      return defaultValue;
    Command command = service.getCommand(id);
    if (command == null)
      return defaultValue;
    State state = command.getState(RegistryToggleState.STATE_ID);
    if (state == null)
      return defaultValue;
    Boolean value = (Boolean) state.getValue();
    if (value == null)
      return defaultValue;
    return value;
  }

  public static ImageDescriptor getImageDescriptor(String pluginId, String path) {
    return AbstractUIPlugin.imageDescriptorFromPlugin(pluginId, path);
  }

  public static Command getCommand(String commandID) {
    ICommandService service = (ICommandService) PlatformUI.getWorkbench().getService(ICommandService.class);
    return service.getCommand(commandID);
  }
}
