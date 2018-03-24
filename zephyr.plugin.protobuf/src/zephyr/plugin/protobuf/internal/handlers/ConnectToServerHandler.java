package zephyr.plugin.protobuf.internal.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.Window;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import zephyr.plugin.core.RunnableFactory;
import zephyr.plugin.core.ZephyrCore;
import zephyr.plugin.network.dialog.ConnectToServerDialog;
import zephyr.plugin.protobuf.ZephyrPluginProtobuf;
import zephyr.plugin.protobuf.internal.network.DirectSocket;
import zephyr.plugin.protobuf.sync.NetworkClientRunnable;

public class ConnectToServerHandler extends AbstractHandler {
  public Object execute(ExecutionEvent event) throws ExecutionException {
    IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindowChecked(event);
    IDialogSettings settings = ZephyrPluginProtobuf.getDefault().getDialogSettings();
    ConnectToServerDialog dialog = new ConnectToServerDialog(window.getShell(), settings);
    dialog.create();
    if (dialog.open() != Window.OK) {
      return null;
    }
    final String hostname = dialog.getHostName();
    final int port = dialog.getPort();
    ZephyrCore.start(new RunnableFactory() {
      @Override
      public Runnable createRunnable() {
        return new NetworkClientRunnable(new DirectSocket(hostname, port));
      }
    });
    return null;
  }
}
