package zephyr.plugin.core.internal.utils;

import java.awt.image.BufferedImage;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.widgets.Canvas;

public class ImageAdapter {
  private ImageData imageData;

  public void paint(GC gc, Canvas canvas) {
    if (imageData == null) {
      gc.fillRectangle(canvas.getBounds());
      return;
    }
    Image image = new Image(canvas.getDisplay(), imageData);
    int width = image.getImageData().width;
    int height = image.getImageData().height;
    gc.drawImage(image, 0, 0, width, height, 0, 0, width, height);
    image.dispose();
  }

  public void update(BufferedImage bufferedImage) {
    imageData = bufferedImage != null ? Images.convertToSWT(bufferedImage) : null;
  }
}
