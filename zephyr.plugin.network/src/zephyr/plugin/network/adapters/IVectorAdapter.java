package zephyr.plugin.network.adapters;

import java.util.List;

public interface IVectorAdapter {
  int size();

  double[] accessData();

  double getEntry(int i);
  
  List<Integer> dimList();

  long id();

  String name();
}
