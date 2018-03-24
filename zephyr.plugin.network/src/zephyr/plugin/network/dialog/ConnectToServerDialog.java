package zephyr.plugin.network.dialog;

import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.dialogs.TitleAreaDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class ConnectToServerDialog extends TitleAreaDialog {
  private static final String HostNameKey = "dialog.connecttoserver.hostname";
  private static final String PortKey = "dialog.connecttoserver.port";
  private Text serverTextBox;
  private Text portTextBox;

  private String hostName = "localhost";
  private int port = 5000;
  private IDialogSettings settings;

  public ConnectToServerDialog(Shell parentShell, IDialogSettings settings) {
    super(parentShell);
    this.settings = settings;
    String storedHostName = settings.get(HostNameKey);
    if (storedHostName != null)
      hostName = storedHostName;
    String storedPort = settings.get(PortKey);
    if (storedPort != null)
      port = Integer.parseInt(storedPort);
  }

  @Override
  public void create() {
    super.create();
    setTitle("Connect to...");
  }

  @Override
  protected Control createDialogArea(Composite parent) {
    Composite area = (Composite) super.createDialogArea(parent);
    Composite container = new Composite(area, SWT.NONE);
    container.setLayoutData(new GridData(GridData.FILL_BOTH));
    GridLayout layout = new GridLayout(2, false);
    container.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
    container.setLayout(layout);

    createServerTextBox(container);
    createPortTextBox(container);

    return area;
  }

  private void createServerTextBox(Composite container) {
    Label label = new Label(container, SWT.NONE);
    label.setText("Host name");
    serverTextBox = new Text(container, SWT.BORDER);
    serverTextBox.setText(hostName);
    setLayoutData(serverTextBox);
  }

  private void setLayoutData(Text textBox) {
    GridData data = new GridData();
    data.grabExcessHorizontalSpace = true;
    data.horizontalAlignment = GridData.FILL;
    textBox.setLayoutData(data);
  }

  private void createPortTextBox(Composite container) {
    Label label = new Label(container, SWT.NONE);
    label.setText("Port");
    portTextBox = new Text(container, SWT.BORDER);
    portTextBox.setText(String.valueOf(port));
    setLayoutData(portTextBox);
    portTextBox.addVerifyListener(new VerifyListener() {
      @Override
      public void verifyText(VerifyEvent event) {
        Text text = (Text) event.getSource();
        final String oldText = text.getText();
        String newText = oldText.substring(0, event.start) + event.text + oldText.substring(event.end);

        boolean isPortNumber = true;
        try {
          int port = Integer.parseInt(newText);
          if (port > 65535)
            isPortNumber = false;
        } catch (NumberFormatException ex) {
          isPortNumber = false;
        }
        if (!isPortNumber)
          event.doit = false;
      }
    });
  }

  @Override
  protected boolean isResizable() {
    return true;
  }

  private void saveInput() {
    hostName = serverTextBox.getText();
    port = Integer.parseInt(portTextBox.getText());
    settings.put(HostNameKey, hostName);
    settings.put(PortKey, port);
  }

  @Override
  protected void okPressed() {
    saveInput();
    super.okPressed();
  }

  public String getHostName() {
    return hostName;
  }

  public int getPort() {
    return port;
  }
}