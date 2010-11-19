package zephyr.plugin.core.api.advertizement;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertizement.Advertizement.Advertized;
import zephyr.plugin.core.api.signals.Listener;

public class AdvertizementTest {
  static private final String infoString = "info";

  static public class StringInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String label, Object advertized, Stack<Object> parents, Object info) {
      return label + "." + (String) info;
    }
  }

  @Advertize(infoProvider = StringInfoProvider.class)
  private final String label01 = "Toto";
  @Advertize(infoProvider = StringInfoProvider.class)
  private final String label02 = "Tata";
  @Advertize(infoProvider = StringInfoProvider.class)
  private final String[] labels = new String[] { "Titi", "Tutu" };

  @Test
  public void testAdvertize() {
    final List<String> advertized = new ArrayList<String>();
    Zephyr.advertizement().onAdvertize.connect(new Listener<Advertizement.Advertized>() {
      @Override
      public void listen(Advertized eventInfo) {
        if (eventInfo.advertized instanceof String)
          advertized.add(String.format("%s[%s]", eventInfo.advertized, eventInfo.info));
      }
    });
    Zephyr.advertize(null, this, infoString);
    checkCollection(new String[] { toAd("label01", label01), toAd("label02", label02),
        toAd("labels", labels[0], 0), toAd("labels", labels[1], 1) }, advertized);
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
