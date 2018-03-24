package zephyr.plugin.protobuf;

import zephyr.protobuf.ZephyrProto;
import zephyr.protobuf.ZephyrProto.NodeDescriptor;

public class PBUtils {
  static public boolean isNumberType(ZephyrProto.NodeDescriptor node) {
    if (ZephyrProto.Type.FLOAT == node.getType()) {
      return true;
    }
    if (ZephyrProto.Type.DOUBLE == node.getType()) {
      return true;
    }
    if (ZephyrProto.Type.INT == node.getType()) {
      return true;
    }
    return false;
  }

  public static boolean isScalar(NodeDescriptor node) {
    return isNumberType(node) && isAtomic(node);
  }

  private static boolean isAtomic(NodeDescriptor node) {
    return node.getDimCount() == 0;
  }
}
