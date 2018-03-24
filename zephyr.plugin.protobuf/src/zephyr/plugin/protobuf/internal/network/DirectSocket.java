package zephyr.plugin.protobuf.internal.network;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteOrder;
import java.util.Arrays;
import zephyr.plugin.network.encoding.LiteByteBuffer;
import zephyr.plugin.protobuf.sync.Connection;
import com.google.protobuf.InvalidProtocolBufferException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public class DirectSocket implements Connection {
  private static final char Separator = '_';
  private Socket socket;
  private final LiteByteBuffer sizeBuffer = new LiteByteBuffer(5, ByteOrder.LITTLE_ENDIAN);
  private int received;
  private final String hostname;
  private final int port;
  private InputStream inputStream;
  private OutputStream outputStream;

  public DirectSocket(String hostname, int port) {
    this.hostname = hostname;
    this.port = port;
  }

  public void connect() throws IOException {
    socket = createSocket(hostname, port);
    if (socket == null)
      return;
    inputStream = socket.getInputStream();
    outputStream = socket.getOutputStream();
  }

  private Socket createSocket(String hostname, int port) {
    System.out.println("Connecting to " + hostname + ":" + port + "...");
    try {
      return new Socket(hostname, port);
    } catch (UnknownHostException e) {
      e.printStackTrace();
    } catch (IOException e) {
      handleError(e);
    }
    return null;
  }

  public MessageLite recvMsv(Parser<? extends MessageLite> parser) {
    int size = readSize();
    if (size < 0)
      return null;
    byte[] data = new byte[size];
    try {
      readAll(data);
    } catch (IOException e1) {
      handleError(e1);
      return null;
    }
    try {
      return parser.parseFrom(data);
    } catch (InvalidProtocolBufferException e) {
      e.printStackTrace();
      System.out.println("data size:" + size);
      System.out.println("data" + Arrays.toString(data));
    }
    return null;
  }

  private void readAll(byte[] data) throws IOException {
    int offset = 0;
    int toRead = data.length - offset;
    while (toRead > 0) {
      received = inputStream.read(data, offset, data.length - offset);
      offset += received;
      toRead = data.length - offset;
    }
  }

  public void sendMsg(MessageLite msg) {
    sizeBuffer.reset();
    byte[] data = msg.toByteArray();
    try {
      sizeBuffer.putInt(data.length);
      sizeBuffer.put((byte) Separator);
      outputStream.write(sizeBuffer.array());
      if (data.length > 0) {
        outputStream.write(data);
      }
      outputStream.flush();
    } catch (IOException e) {
      handleError(e);
    }
  }

  private void handleError(Exception e) {
    e.printStackTrace();
    if (socket != null)
      try {
        socket.close();
      } catch (IOException e1) {
        e1.printStackTrace();
      }
    socket = null;
  }

  private int readSize() {
    sizeBuffer.reset();
    byte[] array = sizeBuffer.array();
    for (int i = 0; i < array.length; i++)
      array[i] = -1;
    try {
      readAll(array);
      int size = sizeBuffer.getInt();
      char c = (char) sizeBuffer.get();
      if (c != Separator) {
        handleError(new RuntimeException("Misaligned data"));
        return -1;
      }
      return size;
    } catch (IOException e) {
      handleError(e);
      return -1;
    }
  }

  public void close() {
    if (socket == null)
      return;
    try {
      socket.close();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public boolean isClosed() {
    if (socket == null)
      return true;
    return socket.isClosed();
  }
}
