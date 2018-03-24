package zephyr.plugin.core.internal.utils;

import java.awt.image.BufferedImage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.widgets.Canvas;

public class ImageAdapter {
  private ImageData imageData;

  public void paint(GC gc, Canvas canvas) {
    Rectangle canvasBounds = canvas.getBounds();
    if (imageData == null) {
      gc.fillRectangle(canvasBounds);
      return;
    }
    Image image = new Image(canvas.getDisplay(), imageData);
    int antiAlias = gc.getAntialias();
    int interpolation = gc.getInterpolation();
    gc.setAntialias(SWT.ON);
    gc.setInterpolation(SWT.HIGH);
    gc.drawImage(image, 0, 0, image.getImageData().width, image.getImageData().height, 0, 0, canvasBounds.width,
                 canvasBounds.height);
    image.dispose();
    gc.setAntialias(antiAlias);
    gc.setInterpolation(interpolation);
  }

  public void update(BufferedImage bufferedImage) {
    imageData = bufferedImage != null ? Images.convertToSWT(bufferedImage) : null;
  }
}
