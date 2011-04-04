package zephyr.plugin.core.api.advertisement;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class AdvertisementTest {
  static private final String infoString = "info";

  static public class StringInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String shortLabel, String fullLabel, Object advertised, Stack<Object> parents, Object info) {
      return fullLabel + "." + (String) info;
    }
  }

  @Advertise(infoProvider = StringInfoProvider.class)
  public final String label01 = "Toto";
  @Advertise(infoProvider = StringInfoProvider.class)
  public final String label02 = "Tata";
  @Advertise(infoProvider = StringInfoProvider.class)
  public final String[] labels = new String[] { "Titi", "Tutu" };

  @Test
  public void testAdvertise() {
    final List<String> advertised = new ArrayList<String>();
    Zephyr.advertisement().onAdvertiseNode.connect(new Listener<Advertisement.Advertised>() {
      @Override
      public void listen(Advertised eventInfo) {
        if (eventInfo.advertised instanceof String)
          advertised.add(String.format("%s[%s]", eventInfo.advertised, eventInfo.info));
      }
    });
    Zephyr.advertise(new Clock(), this, infoString);
    checkCollection(new String[] { toAd("label01", label01), toAd("label02", label02),
        toAd("labels", labels[0], 0), toAd("labels", labels[1], 1) }, advertised);
  }

  private String toAd(String fieldName, String fieldValue) {
    return String.format("%s[%s.%s]", fieldValue, fieldName, infoString);
  }

  private String toAd(String arrayFieldName, String arrayValue, int i) {
    return String.format("%s[%s[%d].%s]", arrayValue, arrayFieldName, i, infoString);
  }

  private void checkCollection(String[] expected, List<String> collection) {
    Assert.assertEquals(expected.length, collection.size());
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], collection.get(i));
  }
}
