package zephyr.plugin.protobuf.adapters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;

public class DoubleVectorAdapter extends VectorAdapter {
  public DoubleVectorAdapter(ProtoBufLoop loop, ZephyrProto.Tree node) {
    super(loop, node);
  }

  @Override
  protected void updateData(byte[] byteData) {
    ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asDoubleBuffer().get(data);
  }
}
