package zephyr.plugin.network.parsers;

import zephyr.plugin.core.api.internal.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeHook;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.FieldParser;
import zephyr.plugin.core.api.internal.codeparser.interfaces.MutableParentNode;
import zephyr.plugin.core.api.internal.parsing.CollectionLabelBuilder;
import zephyr.plugin.network.adapters.DoubleArrayNode;
import zephyr.plugin.network.adapters.HeatmapAdapter;
import zephyr.plugin.network.adapters.IVectorAdapter;
import zephyr.plugin.network.adapters.ImageAdapter;

@SuppressWarnings("restriction")
abstract public class AbstractTensorParser implements FieldParser {
  @Override
  public ClassNode parse(CodeParser codeParser, MutableParentNode parentNode, CodeHook codeHook, String instanceLabel,
      Object instance) {
    NetworkLoop loop = ((TableHook) codeHook).networkLoop();
    IVectorAdapter vectorAdapter = createVectorAdapter(loop, instance);
    if (vectorAdapter == null || vectorAdapter.size() <= 0) {
      return new ClassNode(vectorAdapter.name(), parentNode, instance, codeHook);
    }
    ClassNode tensorNode = createTensorNode(parentNode, codeHook, instanceLabel, loop, vectorAdapter);
    createArrayNode(codeParser, codeHook, loop, vectorAdapter, tensorNode);
    createHeatMapNode(tensorNode, codeHook, loop, vectorAdapter);
    createImageNode(tensorNode, codeHook, loop, vectorAdapter);
    CodeTrees.popupIFN(codeParser, codeHook, tensorNode);
    return tensorNode;
  }

  abstract protected IVectorAdapter createVectorAdapter(NetworkLoop loop, Object instance);

  private void createArrayNode(CodeParser codeParser, CodeHook codeHook, NetworkLoop loop, IVectorAdapter vectorAdapter,
      ClassNode tensorNode) {
    if (vectorAdapter.size() > 100000) {
      return;
    }
    CollectionLabelBuilder labelBuilder = codeParser.newCollectionLabelBuilder(codeHook, vectorAdapter.size());
    DoubleArrayNode arrayNode = new DoubleArrayNode("data", tensorNode, 4, labelBuilder, vectorAdapter);
    loop.registerCodeNode(vectorAdapter.id(), arrayNode);
    tensorNode.addChild(arrayNode);
  }

  private void createHeatMapNode(ClassNode parentNode, CodeHook codeHook, NetworkLoop loop,
      IVectorAdapter vectorAdapter) {
    HeatmapAdapter heatmap = HeatmapAdapter.createHeatmap(vectorAdapter);
    if (heatmap == null) {
      return;
    }
    ClassNode heatmapNode = new ClassNode(parentNode.label(), parentNode, heatmap, codeHook);
    loop.registerCodeNode(vectorAdapter.id(), heatmapNode);
    parentNode.addChild(heatmapNode);
  }

  private void createImageNode(ClassNode parentNode, CodeHook codeHook, NetworkLoop loop,
      IVectorAdapter vectorAdapter) {
    ImageAdapter imageAdapter = ImageAdapter.createImage(vectorAdapter);
    if (imageAdapter == null) {
      return;
    }
    ClassNode imageNode = new ClassNode(parentNode.label(), parentNode, imageAdapter, codeHook);
    loop.registerCodeNode(vectorAdapter.id(), imageNode);
    parentNode.addChild(imageNode);
  }

  private ClassNode createTensorNode(MutableParentNode parentNode, CodeHook codeHook, String instanceLabel,
      NetworkLoop loop, IVectorAdapter vectorAdapter) {
    ClassNode tensorNode = new ClassNode(instanceLabel, parentNode, vectorAdapter, codeHook);
    loop.registerCodeNode(vectorAdapter.id(), tensorNode);
    parentNode.addChild(tensorNode);
    return tensorNode;
  }
}
