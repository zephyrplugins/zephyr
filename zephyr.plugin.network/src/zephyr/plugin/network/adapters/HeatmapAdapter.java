package zephyr.plugin.network.adapters;

import zephyr.plugin.plotting.internal.heatmap.Interval;
import zephyr.plugin.plotting.internal.heatmap.MapData;
import zephyr.plugin.plotting.internal.heatmap.MapDataUpdater;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("restriction")
public class HeatmapAdapter implements MapDataUpdater {
  private final int dimX;
  private final int dimY;
  private final IVectorAdapter vectorAdapter;
  private double minValue;
  private double maxValue;

  public HeatmapAdapter(IVectorAdapter vectorAdapter, int dimX, int dimY) {
    this.vectorAdapter = vectorAdapter;
    this.dimX = dimX;
    this.dimY = dimY;
  }

  @Override
  public void resetRange() {
    minValue = Double.MAX_VALUE;
    maxValue = -Double.MAX_VALUE;
  }

  @Override
  public MapData createNewMapData() {
    return new MapData(dimY, dimX);
  }

  @Override
  public void updateData(MapData data) {
    double[] array = vectorAdapter.accessData();
    if (array == null)
      return;
    for (int i = 0; i < dimX; i++)
      for (int j = 0; j < dimY; j++) {
        int index = i * dimY + j;
        if (index >= array.length)
          break;
        double value = array[index];
        data.imageData()[j][dimX - i - 1] = (float) value;
        if (Double.isNaN(value))
          continue;
        minValue = Math.min(minValue, value);
        maxValue = Math.max(maxValue, value);
      }
    data.setRangeValue(new Interval(minValue, maxValue));
  }

  public static HeatmapAdapter createHeatmap(IVectorAdapter vectorAdapter) {
    List<Integer> dimList = vectorAdapter.dimList();
    if (dimList == null) {
      return null;
    }
    List<Integer> significantDimList = new ArrayList<Integer>();
    for (int i = 0; i < dimList.size(); i++){
    	int dim  = dimList.get(i);
    	if (dim > 1)
    		significantDimList.add(i);
    }
    if (significantDimList.size() != 2) {
      return null;
    }
    return new HeatmapAdapter(vectorAdapter, dimList.get(significantDimList.get(0)), dimList.get(significantDimList.get(1)));
  }
}
