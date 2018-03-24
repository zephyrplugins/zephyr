package zephyr.plugin.protobuf.sync;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import zephyr.plugin.core.api.ParameterizedRunnable;
import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.protobuf.internal.network.DirectSocket;
import zephyr.protobuf.ZephyrProto;
import zephyr.protobuf.ZephyrProto.GetDataResponse;
import zephyr.protobuf.ZephyrProto.GetForestResponse;
import com.google.protobuf.MessageLite;
import com.google.protobuf.Parser;

public class NetworkClientRunnable implements ParameterizedRunnable {
  private final Parser<GetForestResponse>
      getForestResponseParser = ZephyrProto.GetForestResponse.getDefaultInstance().getParserForType();
  private final Parser<ZephyrProto.GetDataResponse>
      dataParser = ZephyrProto.GetDataResponse.getDefaultInstance().getParserForType();
  private final Map<Long, ProtoBufLoop> clockToLoop = new HashMap<Long, ProtoBufLoop>();
  private Connection connection = null;

  public NetworkClientRunnable() {
  }

  public NetworkClientRunnable(Connection connection) {
    this.connection = connection;
  }

  private Connection createConnection() {
    try {
      connection.connect();
    } catch (IOException e) {
      e.printStackTrace();
    }
    if (connection == null || connection.isClosed()) {
      return null;
    }
    return connection;
  }

  @Override
  public void run() {
    Connection connection = createConnection();
    if (connection == null) {
      return;
    }
    connection.sendMsg(ZephyrProto.GetForestRequest.getDefaultInstance());
    ZephyrProto.GetForestResponse forestMsg = (GetForestResponse) connection.recvMsv(getForestResponseParser);
    parseForestMsg(forestMsg);
    for (ProtoBufLoop loop : clockToLoop.values())
      sendDataRequest(connection, loop);
    while (!clockToLoop.isEmpty()) {
      ZephyrProto.GetDataResponse dataMsg = (GetDataResponse) connection.recvMsv(dataParser);
      if (dataMsg == null) {
        break;
      }
      long clockID = dataMsg.getClockId();
      ProtoBufLoop loop = clockToLoop.get(clockID);
      if (loop == null) {
        continue;
      }
      sendDataRequest(connection, loop);
      if (loop.isTerminated()) {
        clockToLoop.remove(clockID);
        continue;
      }
      loop.tick(dataMsg);
    }
    for (ProtoBufLoop loop : clockToLoop.values())
      loop.terminate();
    clockToLoop.clear();
    connection.close();
  }

  private void parseForestMsg(ZephyrProto.GetForestResponse forestMsg) {
    for (ZephyrProto.Forest forest : forestMsg.getForestList()) {
      long clockId = forest.getClock().getId();
      ProtoBufLoop loop = clockToLoop.get(clockId);
      if (loop == null) {
        loop = new ProtoBufLoop(forest.getClock());
        clockToLoop.put(forest.getClock().getId(), loop);
      }
      for (ZephyrProto.Tree tree : forest.getRootList())
        loop.add(tree);
    }
    for (ProtoBufLoop loop : clockToLoop.values())
      Zephyr.advertise(loop.clock(), loop, loop.clockName());
  }

  static private void sendDataRequest(Connection connection, ProtoBufLoop loop) {
    MessageLite msg = loop.buildDataRequestMsg();
    connection.sendMsg(msg);
  }

  @Override
  public void setParameters(String[] parameters) {
    String hostname = parameters.length >= 1 ? parameters[0] : "localhost";
    int port = parameters.length >= 2 ? Integer.parseInt(parameters[1]) : 5000;
    connection = new DirectSocket(hostname, port);
  }
}
