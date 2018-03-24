package zephyr.plugin.plotting.internal.heatmap;

public interface MapDataUpdater {
  MapData createNewMapData();

  void updateData(MapData data);

  void resetRange();
}
