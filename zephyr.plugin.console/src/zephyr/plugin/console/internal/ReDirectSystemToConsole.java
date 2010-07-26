package zephyr.plugin.console.internal;

import java.io.PrintStream;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.MessageConsoleStream;

import zephyr.plugin.console.ZephyrConsolePlugin;
import zephyr.plugin.core.startup.StartupJob;

public class ReDirectSystemToConsole implements StartupJob {
  @Override
  public int level() {
    return 0;
  }

  private void setColor(final MessageConsoleStream consoleStream, final int color) {
    Display.getDefault().syncExec(new Runnable() {
      @Override
      public void run() {
        consoleStream.setColor(Display.getDefault().getSystemColor(color));
      }
    });
  }

  private PrintStream createConsolePipe(final PrintStream defaultOut, int color, boolean activateOnWrite) {
    MessageConsoleStream messageConsoleStream = ZephyrConsolePlugin.getDefault().systemConsole().newMessageStream();
    messageConsoleStream.setActivateOnWrite(activateOnWrite);
    setColor(messageConsoleStream, color);
    PipeStream pipe = new PipeStream(defaultOut, messageConsoleStream);
    PrintStream print = new PrintStream(pipe);
    return print;
  }

  @Override
  public void run() {
    System.setOut(createConsolePipe(System.out, SWT.COLOR_DARK_BLUE, false));
    System.setErr(createConsolePipe(System.err, SWT.COLOR_DARK_RED, true));
  }
}
