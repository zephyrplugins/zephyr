package zephyr.plugin.tests.treeview;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.ZephyrCore;
import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.codetree.ClockNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ImageManager;

public class IconDatabase {
  static interface DataTest {
    boolean testData(Object data);
  }

  static class InstanceOfTest implements DataTest {
    final Class<?> requiredClass;

    InstanceOfTest(Class<?> requiredClass) {
      this.requiredClass = requiredClass;
    }

    @Override
    public boolean testData(Object data) {
      return requiredClass.isInstance(data);
    }
  }

  static class ImageDescriptor {
    final DataTest test;
    final String pluginID;
    final String iconPath;

    ImageDescriptor(DataTest test, String pluginID, String iconPath) {
      this.test = test;
      this.pluginID = pluginID;
      this.iconPath = iconPath;
    }
  }

  private final ImageManager imageManager = new ImageManager();
  private final List<ImageDescriptor> descriptors = new ArrayList<ImageDescriptor>();

  public IconDatabase() {
    descriptors.add(new ImageDescriptor(new InstanceOfTest(ClockNode.class),
                                        ZephyrCore.PluginID, "icons/view_clocks.png"));
    descriptors.add(new ImageDescriptor(createClockTest(),
                                        ZephyrCore.PluginID, "icons/view_clocks.png"));
    descriptors.add(new ImageDescriptor(createRootTest(),
                                        ZephyrCore.PluginID, "icons/view_structureexplorer.gif"));
    // descriptors.add(new ImageDescriptor(new
    // InstanceOfTest(PrimitiveNode.class),
    // ZephyrPlotting.PluginID, "icons/view_plotting.png"));
    descriptors.add(new ImageDescriptor(createCollectionTest(),
                                        ZephyrCore.PluginID, "icons/structure_folder.png"));
  }

  protected InstanceOfTest createCollectionTest() {
    return new InstanceOfTest(ClassNode.class) {
      @Override
      public boolean testData(Object data) {
        if (!requiredClass.isInstance(data))
          return false;
        ClassNode classNode = (ClassNode) data;
        return classNode.instance() instanceof List || classNode.instance().getClass().isArray();
      }
    };
  }

  protected InstanceOfTest createRootTest() {
    return new InstanceOfTest(ClassNode.class) {
      @Override
      public boolean testData(Object data) {
        if (!requiredClass.isInstance(data))
          return false;
        ClassNode classNode = (ClassNode) data;
        return classNode.parent() instanceof ClockNode;
      }
    };
  }

  protected InstanceOfTest createClockTest() {
    return new InstanceOfTest(ClassNode.class) {
      @Override
      public boolean testData(Object data) {
        if (!requiredClass.isInstance(data))
          return false;
        ClassNode classNode = (ClassNode) data;
        return classNode.instance() instanceof Clock;
      }
    };
  }

  public void dispose() {
    imageManager.dispose();
  }

  public void setImage(TreeItem treeItem) {
    Object data = treeItem.getData();
    if (data == null)
      return;
    for (ImageDescriptor descriptor : descriptors) {
      if (!descriptor.test.testData(data))
        continue;
      Image image = imageManager.image(descriptor.pluginID, descriptor.iconPath);
      if (image == null)
        return;
      treeItem.setImage(image);
    }
  }
}
