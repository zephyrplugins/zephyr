package zephyr.plugin.protobuf.sync;

import java.io.IOException;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public interface Connection {

  void connect() throws IOException;

  void sendMsg(MessageLite msg);

  MessageLite recvMsv(Parser<? extends MessageLite> parser);

  boolean isClosed();

  void close();

}
