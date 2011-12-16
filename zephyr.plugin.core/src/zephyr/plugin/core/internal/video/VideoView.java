package zephyr.plugin.core.internal.video;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.api.viewable.ImageProvider;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.utils.ImageAdapter;
import zephyr.plugin.core.views.helpers.ForegroundCanvasView;

public class VideoView extends ForegroundCanvasView<ImageProvider> {
  static public class VideoViewProvider extends ClassViewProvider {
    public VideoViewProvider() {
      super(ImageProvider.class);
    }
  }

  private final ImageAdapter imageAdapter = new ImageAdapter();

  @Override
  protected void paint(GC gc) {
    imageAdapter.paint(gc, canvas);
  }

  @Override
  protected boolean synchronize() {
    return true;
  }

  @Override
  protected void unprotectedSynchronization() {
    ImageProvider provider = instance.current();
    if (provider == null)
      return;
    imageAdapter.update(provider.image());
  }

  @Override
  protected boolean isInstanceSupported(Object instance) {
    return ImageProvider.class.isInstance(instance);
  }
}
