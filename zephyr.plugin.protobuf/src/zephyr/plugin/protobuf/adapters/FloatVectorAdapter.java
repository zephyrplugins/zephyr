package zephyr.plugin.protobuf.adapters;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;

public class FloatVectorAdapter extends VectorAdapter {
  private float[] floatData;

  public FloatVectorAdapter(ProtoBufLoop loop, ZephyrProto.Tree node) {
    super(loop, node);
  }

  @Override
  protected void updateData(byte[] byteData) {
    if (floatData == null || floatData.length != data.length) {
      floatData = new float[data.length];
    }

    ByteBuffer.wrap(byteData).order(ByteOrder.LITTLE_ENDIAN).asFloatBuffer().get(floatData);
    for (int i = 0; i < floatData.length; i++)
      data[i] = floatData[i];
  }
}
