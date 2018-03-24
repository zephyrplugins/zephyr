package zephyr.plugin.protobuf.sync;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.internal.ReferenceManager;
import zephyr.plugin.network.parsers.NetworkLoop;
import zephyr.protobuf.ZephyrProto;
import zephyr.protobuf.ZephyrProto.Data;
import zephyr.protobuf.ZephyrProto.GetDataRequest;
import zephyr.protobuf.ZephyrProto.GetDataRequest.Builder;
import zephyr.protobuf.ZephyrProto.GetDataResponse;
import zephyr.protobuf.ZephyrProto.NodeData;
import zephyr.protobuf.ZephyrProto.NodeDescriptor;
import zephyr.protobuf.ZephyrProto.Tree;

@SuppressWarnings("restriction")
public class ProtoBufLoop implements NetworkLoop {
  private final Clock clock;
  private final List<Tree> trees = new ArrayList<Tree>();
  private final long clock_id;
  private final Map<Long, ZephyrProto.NodeData> idToNode = new HashMap<Long, ZephyrProto.NodeData>();
  private final Map<CodeNode, Long> codeNodeToID = new HashMap<CodeNode, Long>();
  private final Map<Long, List<Listener<ZephyrProto.NodeData>>>
      idToListeners = new HashMap<Long, List<Listener<ZephyrProto.NodeData>>>();

  public ProtoBufLoop(ZephyrProto.Clock clockMsg) {
    clock = new Clock(clockMsg.getName());
    clock.setTimeStep(clockMsg.getTime());
    clock_id = clockMsg.getId();
    // printClock(clockMsg);
  }

  public void add(ZephyrProto.Tree tree) {
    trees.add(tree);
    // printNode(tree.getRoot(), 0);
  }

  void printNode(ZephyrProto.Tree tree, int indentation) {
    StringBuilder indentationStr = new StringBuilder();
    for (int i = 0; i < indentation; i++)
      indentationStr.append(' ');
    StringBuilder types = new StringBuilder();
    NodeDescriptor node = tree.getNodeDescriptor();
    System.out.println(String.format("%sNode ID: %d Name: %s Type: %s", indentationStr.toString(), node.getId(),
                                     node.getName(), node.getType().name()));
    for (ZephyrProto.Tree child : tree.getChildrenList())
      printNode(child, indentation + 1);
  }

  void printClock(ZephyrProto.Clock clock) {
    System.out.println(String.format("Clock ID: %d Name: %s Time: %d", clock.getId(), clock.getName(),
                                     clock.getTime()));
  }

  public long clockID() {
    return clock_id;
  }

  public void tick(GetDataResponse dataMsg) {
    idToNode.clear();
    for (NodeData node : dataMsg.getDataList()) {
      long id = node.getNodeId();
      idToNode.put(id, node);
      List<Listener<NodeData>> listeners = idToListeners.get(id);
      if (listeners == null) {
        continue;
      }
      for (Listener<NodeData> listener : listeners)
        listener.listen(node);
    }
    clock.tick();
  }

  public NodeData getLastNode(long id) {
    return idToNode.get(id);
  }

  public ZephyrProto.GetDataRequest buildDataRequestMsg() {
    ZephyrProto.GetDataRequest.Builder dataRequestBuilder = ZephyrProto.GetDataRequest.newBuilder();
    Builder idsBuilder = dataRequestBuilder.setClockId(clock_id);
    Set<CodeNode> nodes = ReferenceManager.manager().nodes();
    Set<Long> done = new HashSet<Long>();
    for (CodeNode node : nodes) {
      Long id = codeNodeToID.get(node);
      if (id == null || done.contains(id)) {
        continue;
      }
      idsBuilder.addNodeId(id);
      done.add(id);
    }
    GetDataRequest msg = idsBuilder.build();
    return msg;
  }

  public void registerCodeNode(long id, CodeNode codeNode) {
    codeNodeToID.put(codeNode, id);
  }

  public void registerListener(long id, Listener<NodeData> listener) {
    List<Listener<NodeData>> listeners = idToListeners.get(id);
    if (listeners == null) {
      listeners = new ArrayList<Listener<NodeData>>();
      idToListeners.put(id, listeners);
    }
    listeners.add(listener);
  }

  public boolean isTerminated() {
    return clock.isTerminated();
  }

  public void terminate() {
    clock.terminate();
  }

  public List<Tree> trees() {
    return trees;
  }

  public String clockName() {
    return clock.info().label();
  }

  public Clock clock() {
    return clock;
  }

  @Override
  public Double getLastValue(long id) {
    NodeData node = getLastNode(id);
    if (node == null) {
      return null;
    }
    Data data = node.getData();
    if (data.hasBoolValue()) {
      return data.getBoolValue() ? 1.0 : 0.0;
    }
    if (data.hasIntValue()) {
      return (double) data.getIntValue();
    }
    if (data.hasFloatValue()) {
      return (double) data.getFloatValue();
    }
    if (data.hasDoubleValue()) {
      return data.getDoubleValue();
    }
    throw new RuntimeException("Data is not supported");
  }
}
