package zephyr.plugin.protobuf.parsers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.network.parsers.TableHook;
import zephyr.plugin.protobuf.sync.ProtoBufLoop;
import zephyr.protobuf.ZephyrProto;
import zephyr.protobuf.ZephyrProto.NodeDescriptor;

@SuppressWarnings("restriction")
public class TableParser implements FieldParser {

  @Override
  public boolean canParse(Object fieldValue) {
    if (!(fieldValue instanceof ZephyrProto.Tree)) {
      return false;
    }
    return ((ZephyrProto.Tree) fieldValue).getChildrenCount() > 0;
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook codeHook, String instanceLabel,
      Object instance) {
    ZephyrProto.Tree treeMsg = (ZephyrProto.Tree) instance;
    NodeDescriptor nodeMsg = treeMsg.getNodeDescriptor();
    ClassNode node = new ClassNode(nodeMsg.getName(), parentNode, instance, codeHook);
    parentNode.addChild(node);
    ProtoBufLoop loop = (ProtoBufLoop) ((TableHook) codeHook).networkLoop();
    LoopParser.recursiveParseClass(codeParser, loop, node, treeMsg.getChildrenList());
    CodeTrees.popupIFN(codeParser, codeHook, node);
    return node;
  }
}
