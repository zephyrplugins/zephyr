package zephyr.plugin.core.views.helpers;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.ImageLoader;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.PlatformUI;

import zephyr.ZephyrCore;
import zephyr.plugin.core.utils.Helper;

public class ScreenShotAction extends Action {
  public interface Shotable {
    Image takeScreenshot();
  }

  private final Shotable view;

  public ScreenShotAction(Shotable view) {
    super("Screenshot...", IAction.AS_PUSH_BUTTON);
    this.view = view;
    setImageDescriptor(Helper.getImageDescriptor(ZephyrCore.PluginID, "icons/action_screenshot.png"));
  }

  @Override
  public void run() {
    Image screenshot = view.takeScreenshot();
    if (screenshot == null)
      return;
    String filepath = selectFile();
    if (filepath != null) {
      ImageLoader loader = new ImageLoader();
      loader.data = new ImageData[] { screenshot.getImageData() };
      loader.save(filepath, SWT.IMAGE_PNG);
    }
    screenshot.dispose();
  }

  private String selectFile() {
    Shell shell = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell();
    FileDialog fd = new FileDialog(shell, SWT.SAVE);
    fd.setText("Save Image");
    fd.setFilterExtensions(new String[] { "*.png" });
    fd.setFileName("screenshot.png");
    return fd.open();
  }
}