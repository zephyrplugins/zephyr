package zephyr.plugin.plotting.internal.histories;

import org.junit.Assert;
import org.junit.Test;

public class HistoryTest {
  static private final float[] hist00 = { 0.0f, 0.0f, 0.0f };
  static private final float[] hist01 = { 0.0f, 0.0f, 1.0f };
  static private final float[] hist02 = { 0.0f, 1.0f, 2.0f };
  static private final float[] hist03 = { 1.0f, 2.0f, 3.0f };
  static private final float[] hist04 = { 2.0f, 3.0f, 4.0f };

  @Test
  public void testHistory() {
    History h = new History(3);
    assertEquals(hist00, h.toArray());
    h.append(1.0);
    assertEquals(hist01, h.toArray());
    h.append(2.0);
    assertEquals(hist02, h.toArray());
    h.append(3.0);
    assertEquals(hist03, h.toArray());
    h.append(4.0);
    assertEquals(hist04, h.toArray());
  }

  @Test
  public void testLazyHistory01() {
    History h = new LazyHistory(3);
    assertEquals(hist00, h.toArray());
    h.append(1.0);
    assertEquals(hist01, h.toArray());
    h.append(2.0);
    assertEquals(hist02, h.toArray());
    h.append(3.0);
    assertEquals(hist03, h.toArray());
    h.append(4.0);
    assertEquals(hist04, h.toArray());
  }

  @Test
  public void testLazyHistory02() {
    final int length = 100;
    final int subLength = 25;
    History h = new LazyHistory(length);
    for (int i = 0; i <= subLength; i++)
      h.append(i);
    Assert.assertEquals((subLength * subLength + subLength) / 2, h.sum(), 0.0);
    float[] expected = new float[length];
    for (int i = 0; i <= subLength; i++)
      expected[length - i - 1] = subLength - i;
    assertEquals(expected, h.toArray());
    Assert.assertTrue(h.capacity() < subLength * 1.5);
  }

  @Test
  public void testAveragedHistoryWithPeriod01() {
    History h = new AveragedHistory(1, 3);
    assertEquals(hist00, h.toArray());
    h.append(1.0);
    assertEquals(hist01, h.toArray());
    h.append(2.0);
    assertEquals(hist02, h.toArray());
    h.append(3.0);
    assertEquals(hist03, h.toArray());
    h.append(4.0);
    assertEquals(hist04, h.toArray());
  }

  @Test
  public void testAveragedHistoryWithPeriod02() {
    History h = new AveragedHistory(2, 3);
    assertEquals(hist00, h.toArray());

    h.append(0.0);
    assertEquals(hist00, h.toArray());
    h.append(2.0);
    assertEquals(hist01, h.toArray());

    h.append(2.0);
    assertEquals(hist01, h.toArray());
    h.append(2.0);
    assertEquals(hist02, h.toArray());

    h.append(1.0);
    assertEquals(hist02, h.toArray());
    h.append(5.0);
    assertEquals(hist03, h.toArray());

    h.append(2.0);
    assertEquals(hist03, h.toArray());
    h.append(6.0);
    assertEquals(hist04, h.toArray());
  }

  private void assertEquals(float[] expected, float[] actual) {
    Assert.assertEquals(expected.length, actual.length);
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], actual[i], 0.0);
  }
}
