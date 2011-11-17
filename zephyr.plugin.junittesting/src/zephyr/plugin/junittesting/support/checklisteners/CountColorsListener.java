package zephyr.plugin.junittesting.support.checklisteners;

import java.util.HashSet;
import java.util.Set;

import org.eclipse.swt.graphics.GC;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Control;

import zephyr.plugin.core.async.events.Event;
import zephyr.plugin.core.async.listeners.UIListener;

public class CountColorsListener extends UIListener {
  @Override
  protected void listenInUIThread(Event event) {
    CheckEvent checkEvent = (CheckEvent) event;
    Control control = ControlChecks.findControl(checkEvent.viewID());
    if (control == null)
      return;
    Image image = takeScreenshot(control);
    Set<Integer> colors = new HashSet<Integer>();
    findColors(colors, image);
    checkEvent.setResult(colors.size());
  }

  private void findColors(Set<Integer> colors, Image image) {
    ImageData imageData = image.getImageData();
    for (int x = 0; x < imageData.width; x++)
      for (int y = 0; y < imageData.height; y++)
        colors.add(imageData.getPixel(x, y));
  }

  private Image takeScreenshot(Control control) {
    control.update();
    Point controlSize = control.getSize();
    GC gc = new GC(control);
    Image image = new Image(control.getDisplay(), controlSize.x, controlSize.y);
    gc.copyArea(image, 0, 0);
    gc.dispose();
    return image;
  }
}
