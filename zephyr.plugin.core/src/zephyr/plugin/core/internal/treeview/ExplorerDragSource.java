package zephyr.plugin.core.internal.treeview;

import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Tree;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.views.CodeNodeTransfert;

public class ExplorerDragSource implements DragSourceListener {
  public ExplorerDragSource() {
  }

  public void setTree(Tree tree) {
    int operations = DND.DROP_MOVE | DND.DROP_COPY;
    DragSource source = new DragSource(tree, operations);
    Transfer[] types = new Transfer[] { CodeNodeTransfert.instance() };
    source.setTransfer(types);
    source.addDragListener(this);
  }

  @Override
  public void dragStart(DragSourceEvent event) {
    Tree tree = (Tree) ((DragSource) event.getSource()).getControl();
    CodeNode[] codeNodes = StructureExplorerView.getSelection(tree);
    CodeNodeTransfert.instance().setSelection(codeNodes);
  }

  @Override
  public void dragSetData(DragSourceEvent event) {
  }

  @Override
  public void dragFinished(DragSourceEvent event) {
    CodeNodeTransfert.instance().setSelection(null);
  }
}
