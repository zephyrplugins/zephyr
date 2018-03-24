package zephyr.plugin.protobuf.parsers;

import java.util.List;
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
public class LoopParser implements FieldParser {

  @Override
  public boolean canParse(Object fieldValue) {
    return fieldValue instanceof ProtoBufLoop;
  }

  @Override
  public CodeNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook instanceField,
      String instanceLabel, Object instance) {
    ProtoBufLoop protoBufLoop = (ProtoBufLoop) instance;
    ClassNode loopNode = new ClassNode(protoBufLoop.clockName(), parentNode, instance, instanceField);
    for (ZephyrProto.Tree tree : protoBufLoop.trees()) {
      ZephyrProto.NodeDescriptor node = tree.getNodeDescriptor();
      ClassNode treeNode = new ClassNode(node.getName(), loopNode, instance, instanceField);
      loopNode.addChild(treeNode);
      if (tree.getChildrenCount() == 0) {
        codeParser.recursiveParseInstance(treeNode, new TableHook(protoBufLoop, node.getName()), node.getName(), node);
      } else {
        recursiveParseClass(codeParser, protoBufLoop, treeNode, tree.getChildrenList());
      }
      CodeTrees.popupIFN(codeParser, instanceField, treeNode);
    }
    return loopNode;
  }

  static void recursiveParseClass(CodeParser codeParser, ProtoBufLoop protoBufLoop, ClassNode classNode,
      List<ZephyrProto.Tree> children) {
    for (ZephyrProto.Tree child : children) {
      NodeDescriptor node = child.getNodeDescriptor();
      codeParser.recursiveParseInstance(classNode, new TableHook(protoBufLoop, node.getName()), node.getName(), child);
    }
  }
}
