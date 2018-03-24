package zephyr.plugin.protobuf.adapters;


import java.util.List;
import zephyr.plugin.core.api.monitoring.abstracts.MultiDimArray;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.network.adapters.IVectorAdapter;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;
import com.google.protobuf.ByteString;

abstract public class VectorAdapter implements MultiDimArray, Listener<ZephyrProto.NodeData>, IVectorAdapter {
  protected double[] data = null;
  private int size;
  private List<Integer> dimList;
  private final String nodeName;
  private final long id;

  public VectorAdapter(ProtoBufLoop loop, ZephyrProto.Tree node) {
    loop.registerListener(node.getNodeDescriptor().getId(), this);
    dimList = node.getNodeDescriptor().getDimList();
    size = getDimension(dimList);
    nodeName = node.getNodeDescriptor().getName();
    id = node.getNodeDescriptor().getId();
  }

  final protected List<Integer> getDimList(ZephyrProto.Data data) {
    return data.getDimList();
  }

  @Override
  public int size() {
    return size;
  }

  private int getDimension(List<Integer> dimList) {
    if (dimList == null || dimList.size() == 0) {
      return -1;
    }
    int dim = 1;
    for (Integer d_i : dimList)
      dim *= d_i;
    return dim;
  }

  @Override
  public double[] accessData() {
    return data;
  }

  @Override
  public void listen(ZephyrProto.NodeData dataMsg) {
    dimList = getDimList(dataMsg.getData());
    int sizeInMsg = getDimension(dimList);
    if (sizeInMsg < 0) {
      return;
    }
    size = sizeInMsg;
    if (data == null || data.length != size) {
      data = new double[size];
    }
    ByteString array = dataMsg.getData().getRawData();
    if (array == null) {
      return;
    }
    if (array.size() == 0) {
      return;
    }
    updateData(array.toByteArray());
  }

  @Override
  public double getEntry(int i) {
    if (data != null && data.length > i) {
      return data[i];
    }
    return 0;
  }

  abstract protected void updateData(byte[] byteData);

  @Override
  public List<Integer> dimList() {
    return dimList;
  }

  @Override
  public long id() {
    return id;
  }

  @Override
  public String name() {
    return nodeName;
  }

  @Override
  public int[] dim() {
    if (dimList == null) {
      return null;
    }
    Object[] dimInt = dimList.toArray();
    int[] dim = new int[dimInt.length];
    for (int i = 0; i < dim.length; i++)
      dim[i] = (Integer) dimInt[i];
    return dim;
  }
}
