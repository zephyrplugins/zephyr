package zephyr.plugin.core.internal.treeview;

import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.helpers.ImageManager;
import zephyr.plugin.core.internal.ZephyrPluginCore;
import zephyr.plugin.core.internal.synchronization.providers.ViewProviderReference;

public class IconDatabase {
  private final ImageManager imageManager = new ImageManager();

  public IconDatabase() {
  }

  private Image findDefaultIcon(CodeNode codeNode) {
    if (codeNode.parent() instanceof ClockNode)
      return imageManager.image(ZephyrCore.PluginID, "icons/view_structureexplorer.gif");
    if (!(codeNode instanceof ClassNode))
      return null;
    Object instance = ((ClassNode) codeNode).instance();
    if (instance instanceof List || instance.getClass().isArray())
      return imageManager.image(ZephyrCore.PluginID, "icons/structure_folder.png");
    return null;
  }

  private Image findImageFromProviders(CodeNode codeNode) {
    List<ViewProviderReference> providers = ZephyrPluginCore.viewBinder().findViewProviders(codeNode);
    if (providers.isEmpty())
      return null;
    ViewProviderReference reference = providers.get(0);
    String iconPath = reference.iconPath();
    if (iconPath == null)
      return imageManager.image(ZephyrCore.PluginID, "icons/structure_default.gif");
    String pluginID = reference.pluginID();
    return imageManager.image(pluginID, iconPath);
  }

  public void setImage(TreeItem treeItem) {
    Object data = treeItem.getData();
    if (data == null || !(data instanceof CodeNode))
      return;
    CodeNode codeNode = (CodeNode) data;
    Image image = findImageFromProviders(codeNode);
    if (image == null)
      image = findDefaultIcon(codeNode);
    if (image != null)
      treeItem.setImage(image);
  }

  public void dispose() {
    imageManager.dispose();
  }
}
