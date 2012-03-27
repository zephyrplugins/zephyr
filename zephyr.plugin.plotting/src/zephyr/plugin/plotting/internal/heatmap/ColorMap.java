package zephyr.plugin.plotting.internal.heatmap;

import org.eclipse.swt.graphics.RGB;

public class ColorMap {
  private final int[][] landmarks;
  private final int[][] diffs;
  private final Interval[] colorRanges;
  private final RGB spriteColor;

  public ColorMap(ColorMapDescriptor descriptor) {
    this.landmarks = descriptor.landmarks;
    diffs = colorPreprocessing(landmarks);
    colorRanges = rangePreprocessing(landmarks, 1.0 / diffs.length);
    spriteColor = new RGB(descriptor.spriteColor[0], descriptor.spriteColor[1], descriptor.spriteColor[2]);
  }

  public int nbColors() {
    return 0;
  }

  private Interval[] rangePreprocessing(int[][] landmarks, double colorResolution) {
    Interval[] ranges = new Interval[landmarks.length - 1];
    for (int i = 0; i < ranges.length; i++)
      ranges[i] = new Interval(i * colorResolution, (i + 1) * colorResolution);
    return ranges;
  }

  private int[][] colorPreprocessing(int[][] landmarks) {
    int[][] diffs = new int[landmarks.length - 1][];
    for (int i = 0; i < diffs.length; i++)
      diffs[i] = computeDiff(landmarks[i + 1], landmarks[i]);
    return diffs;
  }

  private int[] computeDiff(int[] colorA, int[] colorB) {
    return new int[] { colorA[0] - colorB[0], colorA[1] - colorB[1], colorA[2] - colorB[2] };
  }


  static private int colorToInt(int r, int g, int b) {
    return 0xFF000000 | (r << 16) | (g << 8) | b;
  }

  public int valueToColor(double value) {
    double adjustedValue = Math.min(value, 1.0 - 1e-10);
    int colorIndex = (int) Math.floor(adjustedValue * (landmarks.length - 1));
    int[] minColor = landmarks[colorIndex];
    int[] diffColor = diffs[colorIndex];
    Interval colorRange = colorRanges[colorIndex];
    double scaledValue = colorRange.scale(adjustedValue);
    return colorToInt(minColor[0] + (int) (scaledValue * diffColor[0]), minColor[1]
        + (int) (scaledValue * diffColor[1]), minColor[2] + (int) (scaledValue * diffColor[2]));
  }

  public RGB spriteColor() {
    return spriteColor;
  }
}
