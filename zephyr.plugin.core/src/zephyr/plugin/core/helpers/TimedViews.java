package zephyr.plugin.core.helpers;

import java.util.Arrays;

import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;

public class TimedViews {
  public static boolean[] toBooleans(CodeNode[] codeNodes, int displayedIndex) {
    boolean[] result = new boolean[codeNodes.length];
    Arrays.fill(result, false);
    if (displayedIndex != -1)
      result[displayedIndex] = true;
    return result;
  }
}
