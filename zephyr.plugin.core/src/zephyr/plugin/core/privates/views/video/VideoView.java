package zephyr.plugin.core.privates.views.video;

import org.eclipse.swt.graphics.GC;

import zephyr.plugin.core.api.viewable.ImageProvider;
import zephyr.plugin.core.internal.helpers.ClassViewProvider;
import zephyr.plugin.core.internal.utils.ImageAdapter;
import zephyr.plugin.core.internal.views.helpers.ForegroundCanvasView;

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
