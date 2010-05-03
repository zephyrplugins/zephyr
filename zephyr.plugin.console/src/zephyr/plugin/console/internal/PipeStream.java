package zephyr.plugin.console.internal;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;

import org.eclipse.ui.console.MessageConsoleStream;

public class PipeStream extends OutputStream {
  private final PrintStream printStream;
  private final MessageConsoleStream messageConsoleStream;

  public PipeStream(PrintStream printStream, MessageConsoleStream messageConsoleStream) {
    this.printStream = printStream;
    this.messageConsoleStream = messageConsoleStream;
  }

  @Override
  public void write(int b) throws IOException {
    printStream.write(b);
    messageConsoleStream.write(b);
  }
}
