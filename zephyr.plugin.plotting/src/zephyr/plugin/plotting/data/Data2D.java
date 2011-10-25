package zephyr.plugin.plotting.data;


public class Data2D {
  public final String label;
  public final float[] xdata;
  public final float[] ydata;
  public int nbPoints;

  public Data2D(int nbPoints) {
    this("", nbPoints);
  }

  public Data2D(String label, int nbPoints) {
    this.label = label;
    xdata = new float[nbPoints];
    ydata = new float[nbPoints];
    this.nbPoints = nbPoints;
  }
}
