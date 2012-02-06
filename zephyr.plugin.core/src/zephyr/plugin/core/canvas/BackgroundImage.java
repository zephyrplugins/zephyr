package zephyr.plugin.core.canvas;

import org.eclipse.swt.graphics.Device;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Rectangle;

public class BackgroundImage {
  private Image image = null;
  private GC gc = null;

  public boolean needUpdate(Rectangle dimension) {
    if (image == null)
      return true;
    return image.getBounds().height != dimension.height || image.getBounds().width != dimension.width;
  }

  public Image image() {
    return image;
  }

  public GC acquireGC(Device device, Rectangle region) {
    if (needUpdate(region))
      createNewImage(device, region);
    assert gc == null;
    gc = new GC(image);
    return gc;
  }

  private void createNewImage(Device device, Rectangle region) {
    if (image != null)
      image.dispose();
    image = new Image(device, region.width, region.height);
  }

  public void releaseGC() {
    gc.dispose();
    gc = null;
  }
}
