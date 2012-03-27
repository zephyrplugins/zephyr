package zephyr.plugin.core.internal.helpers;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

import zephyr.plugin.core.internal.utils.Helper;

public class ImageManager {
  private final Map<String, Map<String, Image>> loadedImages = new HashMap<String, Map<String, Image>>();

  synchronized public Image image(String pluginID, String iconPath) {
    Map<String, Image> pluginImages = loadedImages.get(pluginID);
    if (pluginImages == null) {
      pluginImages = new HashMap<String, Image>();
      loadedImages.put(pluginID, pluginImages);
    }
    Image image = pluginImages.get(iconPath);
    if (image == null) {
      ImageDescriptor imageDescriptor = Helper.getImageDescriptor(pluginID, iconPath);
      if (imageDescriptor == null)
        return null;
      image = imageDescriptor.createImage();
      pluginImages.put(iconPath, image);
    }
    return image;
  }

  synchronized public void dispose() {
    for (Map<String, Image> pluginImages : loadedImages.values()) {
      for (Image image : pluginImages.values())
        image.dispose();
      pluginImages.clear();
    }
  }
}
