package zephyr.plugin.plotting.internal.heatmap;

public class MapData {
  final public int resolution;
  private final float[][] imageData;
  private Interval rangeValue = new Interval(0, 0);

  public MapData(int resolution) {
    this.resolution = resolution;
    this.imageData = createImageData();
  }

  private float[][] createImageData() {
    float[][] imageData = new float[resolution][];
    for (int i = 0; i < imageData.length; i++)
      imageData[i] = new float[resolution];
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
}
