package zephyr.plugin.protobuf.adapters;

import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;

public class ByteVectorAdapter extends VectorAdapter {
  public ByteVectorAdapter(ProtoBufLoop loop, ZephyrProto.Tree node) {
    super(loop, node);
  }

  @Override
  protected void updateData(byte[] byteData) {
    assert (byteData.length == data.length);
    for (int i = 0; i < byteData.length; i++)
      data[i] = byteData[i];
  }
}
