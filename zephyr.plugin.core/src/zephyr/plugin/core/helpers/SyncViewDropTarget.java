package zephyr.plugin.core.helpers;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Control;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.views.CodeNodeTransfert;
import zephyr.plugin.core.views.DropTargetView;

public class SyncViewDropTarget implements DropTargetListener {
  private final DropTargetView targetView;

  public SyncViewDropTarget(DropTargetView targetView, Control control) {
    this.targetView = targetView;
    int operations = DND.DROP_COPY | DND.DROP_MOVE | DND.DROP_DEFAULT;
    DropTarget target = new DropTarget(control, operations);
    Transfer[] types = new Transfer[] { CodeNodeTransfert.instance() };
    target.setTransfer(types);
    target.addDropListener(this);
  }

  @Override
  public void dragEnter(DropTargetEvent event) {
    if (!(targetView instanceof ViewPart))
      return;
    ViewPart part = (ViewPart) targetView;
    CodeNode[] supported = supportedTransferCodeNode(event);
    if (supported.length == 0)
      return;
    part.getSite().getPage().activate(part);
  }

  @Override
  public void dragLeave(DropTargetEvent event) {
  }

  @Override
  public void dragOperationChanged(DropTargetEvent event) {
  }

  @Override
  public void dragOver(DropTargetEvent event) {
  }

  @Override
  public void drop(DropTargetEvent event) {
    CodeNode[] supported = supportedTransferCodeNode(event);
    if (supported.length == 0)
      return;
    targetView.drop(supported);
  }

  private CodeNode[] supportedTransferCodeNode(DropTargetEvent event) {
    CodeNodeTransfert transfer = CodeNodeTransfert.instance();
    boolean isSupported = false;
    for (int i = 0; i < event.dataTypes.length; i++) {
      if (transfer.isSupportedType(event.dataTypes[i])) {
        isSupported = true;
        break;
      }
    }
    if (!isSupported)
      return new CodeNode[] {};
    return filterCodeNodes(transfer);
  }

  private CodeNode[] filterCodeNodes(CodeNodeTransfert transfer) {
    CodeNode[] codeNodes = transfer.codeNodes();
    List<CodeNode> supportedCodeNodes = new ArrayList<CodeNode>();
    for (CodeNode codeNode : codeNodes)
      if (targetView.isSupported(codeNode))
        supportedCodeNodes.add(codeNode);
    CodeNode[] result = new CodeNode[supportedCodeNodes.size()];
    supportedCodeNodes.toArray(result);
    return result;
  }

  @Override
  public void dropAccept(DropTargetEvent event) {
  }
}
