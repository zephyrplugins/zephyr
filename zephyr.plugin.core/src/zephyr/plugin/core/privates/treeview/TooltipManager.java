package zephyr.plugin.core.privates.treeview;

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;

import zephyr.plugin.core.api.internal.codeparser.codetree.CodeTrees;
import zephyr.plugin.core.api.internal.codeparser.interfaces.CodeNode;

public class TooltipManager implements Listener {
  final private Listener labelListener = new Listener() {
    @Override
    public void handleEvent(Event event) {
      Label label = (Label) event.widget;
      Shell shell = label.getShell();
      switch (event.type) {
      case SWT.MouseDown:
        // setSelection(new StructuredSelection(label.getData("_TOOLTIP")));
        break;
      case SWT.MouseExit:
        shell.dispose();
        break;
      }
    }
  };
  private final Tree tree;
  private Shell tip = null;
  private Label label = null;

  public TooltipManager(Tree tree) {
    this.tree = tree;
    tree.setToolTipText("");
    tree.addListener(SWT.KeyDown, this);
    tree.addListener(SWT.MouseMove, this);
    tree.addListener(SWT.MouseHover, this);
    tree.addListener(SWT.MouseHorizontalWheel, this);
    tree.addListener(SWT.MouseVerticalWheel, this);
    tree.addListener(SWT.MouseExit, this);
  }

  @Override
  public void handleEvent(Event event) {
    switch (event.type) {
    case SWT.MouseHorizontalWheel:
      disposeTooltip();
      break;
    case SWT.MouseVerticalWheel:
      disposeTooltip();
      break;
    case SWT.MouseMove: {
      if (tip == null)
        break;
      TreeItem item = tree.getItem(new Point(event.x, event.y));
      if (item == null) {
        disposeTooltip();
        break;
      }
      if (label != null && !label.isDisposed()) {
        Object data = item.getData();
        if (data == label.getData("_TOOLTIP"))
          break;
      }
      break;
    }
    case SWT.KeyDown:
      disposeTooltip();
      break;
    case SWT.MouseExit:
      disposeTooltip();
      break;
    case SWT.MouseHover: {
      TreeItem item = tree.getItem(new Point(event.x, event.y));
      if (item != null) {
        if (tip != null && !tip.isDisposed())
          tip.dispose();
        Object data = item.getData();
        if (data != null) {
          String tooltip = CodeTrees.nodeInfo((CodeNode) data);
          if (tooltip != null && !tooltip.isEmpty()) {
            tip = new Shell(tree.getShell(), SWT.ON_TOP | SWT.TOOL);
            FillLayout fillLayout = new FillLayout();
            fillLayout.marginHeight = 1;
            fillLayout.marginWidth = 1;
            tip.setLayout(fillLayout);
            label = new Label(tip, SWT.NONE);
            label.setForeground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_FOREGROUND));
            label.setBackground(tip.getDisplay().getSystemColor(SWT.COLOR_INFO_BACKGROUND));
            label.setData("_TOOLTIP", data);
            label.setText(tooltip);
            label.addListener(SWT.MouseExit, labelListener);
            label.addListener(SWT.MouseDown, labelListener);
            Point size = tip.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            Point pt = tree.toDisplay(event.x, event.y);
            tip.setBounds(pt.x, pt.y + 26, size.x, size.y);
            tip.setVisible(true);
          }
        }
      }
    }
    }
  }

  private void disposeTooltip() {
    if (tip == null)
      return;
    tip.dispose();
    tip = null;
    label = null;
  }
}