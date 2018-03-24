package zephyr.plugin.protobuf.parsers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.network.adapters.IVectorAdapter;
import zephyr.plugin.network.parsers.AbstractTensorParser;
import zephyr.plugin.network.parsers.NetworkLoop;
import zephyr.plugin.protobuf.PBUtils;
import zephyr.plugin.protobuf.adapters.ByteVectorAdapter;
import zephyr.plugin.protobuf.adapters.DoubleVectorAdapter;
import zephyr.plugin.protobuf.adapters.FloatVectorAdapter;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;
import zephyr.protobuf.ZephyrProto.NodeDescriptor;
import zephyr.protobuf.ZephyrProto.Tree;
import zephyr.protobuf.ZephyrProto.Type;

@SuppressWarnings("restriction")
public class TensorParser extends AbstractTensorParser {
  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof ZephyrProto.Tree)) {
      return false;
    }
    Tree tree = (Tree) fieldValue;
    NodeDescriptor node = tree.getNodeDescriptor();
    return PBUtils.isNumberType(node) && !PBUtils.isScalar(node);
  }

  @Override
  protected IVectorAdapter createVectorAdapter(NetworkLoop loop, Object instance) {
    Type type = ((Tree) instance).getNodeDescriptor().getType();
    if (ZephyrProto.Type.INT == type) {
      return new ByteVectorAdapter((ProtoBufLoop) loop, (Tree) instance);
    }
    if (ZephyrProto.Type.FLOAT == type) {
      return new FloatVectorAdapter((ProtoBufLoop) loop, (Tree) instance);
    }
    if (ZephyrProto.Type.DOUBLE == type) {
      return new DoubleVectorAdapter((ProtoBufLoop) loop, (Tree) instance);
    }
    return null;
  }

  @Override
  public ClassNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook codeHook, String instanceLabel,
      Object instance) {
    ClassNode tensorNode = super.parse(codeParser, parentNode, codeHook, instanceLabel, instance);
    Tree tree = (Tree) instance;
    for (ZephyrProto.Tree child : tree.getChildrenList()) {
      NodeDescriptor node = child.getNodeDescriptor();
      codeParser.recursiveParseInstance(tensorNode, codeHook, node.getName(), child);
    }
    return tensorNode;
  }
}
