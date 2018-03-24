package zephyr.plugin.network.adapters;

import zephyr.plugin.core.api.viewable.ImageProvider;
import zephyr.plugin.core.internal.utils.Colors;

import java.awt.image.BufferedImage;
import java.util.List;

@SuppressWarnings("restriction")
public class ImageAdapter implements ImageProvider {
  private final int dimX;
  private final int dimY;
  private final IVectorAdapter vectorAdapter;
  private final BufferedImage image;
  private final int nPixels;

  public ImageAdapter(IVectorAdapter vectorAdapter, int dimX, int dimY) {
    this.vectorAdapter = vectorAdapter;
    this.dimX = dimX;
    this.dimY = dimY;
    image = new BufferedImage(dimX, dimY, BufferedImage.TYPE_INT_ARGB);
    nPixels = dimX * dimY;
  }

  @Override
  public BufferedImage image() {
    double[] data = vectorAdapter.accessData();
    if (data == null)
      return null;
    double scale = computeScale(data);
    for (int x = 0; x < dimX; x++) {
      for (int y = 0; y < dimY; y++) {
        int offset = y * dimX + x;
        int red = (int) Math.floor(data[offset] * scale);
        int blue = (int) Math.floor(data[nPixels + offset] * scale);
        int green = (int) Math.floor(data[nPixels * 2 + offset] * scale);
        image.setRGB(x, y, Colors.colorToInt(red, blue, green));
      }
    }
    return image;
  }

  private double computeScale(double[] data) {
    int i = 0;
    while (i < data.length) {
      if (data[i] > 1)
        return 1;
      if (data[i] > 0 && data[i] < 1)
        return 255;
      i++;
    }
    return 0;
  }
  
  public static ImageAdapter createImage(IVectorAdapter vectorAdapter) {
    List<Integer> dimList = vectorAdapter.dimList();
    if (dimList == null) {
      return null;
    }
    if (dimList.size() != 3 || dimList.get(0) != 3) {
      return null;
    }
    return new ImageAdapter(vectorAdapter, dimList.get(2), dimList.get(1));
  }
}
