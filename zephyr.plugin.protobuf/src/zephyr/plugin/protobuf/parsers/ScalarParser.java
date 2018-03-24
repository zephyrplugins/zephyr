package zephyr.plugin.protobuf.parsers;

import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.codetree.PrimitiveNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.network.parsers.ScalarHook;
import zephyr.plugin.network.parsers.TableHook;
import zephyr.plugin.protobuf.PBUtils;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;

@SuppressWarnings("restriction")
public class ScalarParser implements FieldParser {

  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof ZephyrProto.Tree)) {
      return false;
    }
    ZephyrProto.Tree tree = ((ZephyrProto.Tree) fieldValue);
    if (tree.getChildrenCount() > 0) {
      return false;
    }
    return PBUtils.isScalar(tree.getNodeDescriptor());
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook codeHook, String instanceLabel,
      Object instance) {
    ZephyrProto.NodeDescriptor nodeMsg = ((ZephyrProto.Tree) instance).getNodeDescriptor();
    ProtoBufLoop loop = (ProtoBufLoop) ((TableHook) codeHook).networkLoop();
    ScalarHook scalarHook = new ScalarHook(loop, nodeMsg.getName(), nodeMsg.getId());
    PrimitiveNode leaf = new PrimitiveNode(instanceLabel, parentNode, scalarHook, instance, 0);
    loop.registerCodeNode(nodeMsg.getId(), leaf);
    parentNode.addChild(leaf);
    CodeTrees.popupIFN(codeParser, codeHook, leaf);
    return leaf;
  }
}
