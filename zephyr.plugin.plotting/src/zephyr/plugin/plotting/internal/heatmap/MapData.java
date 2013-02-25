package zephyr.plugin.plotting.internal.heatmap;

public class MapData {
  final public int resolutionX;
  final public int resolutionY;
  private final float[][] imageData;
  private Interval rangeValue = new Interval(0, 0);

  public MapData(int resolution) {
    this(resolution, resolution);
  }

  public MapData(int resolutionX, int resolutionY) {
    this.resolutionX = resolutionX;
    this.resolutionY = resolutionY;
    this.imageData = createImageData();
  }

  private float[][] createImageData() {
    float[][] imageData = new float[resolutionX][];
    for (int i = 0; i < imageData.length; i++)
      imageData[i] = new float[resolutionY];
    return imageData;
  }

  public void setRangeValue(Interval rangeValue) {
    this.rangeValue = rangeValue;
  }

  public Interval rangeValue() {
    return rangeValue;
  }

  public float[][] imageData() {
    return imageData;
  }

  public MapData copy() {
    MapData result = new MapData(resolutionX, resolutionY);
    for (int i = 0; i < imageData.length; i++)
      System.arraycopy(imageData[i], 0, result.imageData[i], 0, imageData[i].length);
    return result;
  }
}
