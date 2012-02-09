package zephyr.plugin.plotting.heatmap;

public class ColorMapDescriptor {
  final int[][] landmarks;
  final int[] spriteColor;

  public ColorMapDescriptor(int[][] landmarks, int[] spriteColor) {
    this.landmarks = landmarks;
    this.spriteColor = spriteColor;
  }
}
