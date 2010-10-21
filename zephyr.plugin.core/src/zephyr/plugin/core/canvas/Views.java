package zephyr.plugin.core.canvas;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;

public class Views {
  static public void setLayoutData(Control control) {
    GridLayout gridLayout = new GridLayout(1, false);
    control.getParent().setLayout(gridLayout);
    control.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
  }
}
