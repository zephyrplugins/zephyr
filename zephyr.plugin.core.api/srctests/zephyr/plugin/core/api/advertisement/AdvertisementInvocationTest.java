package zephyr.plugin.core.api.advertisement;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;

@Advertise
public class AdvertisementInvocationTest {
  static public class InfoProviderChild {
    public String method(String label, Object advertised, Object info, AdvertisementInvocationTest parent) {
      return getClass().getSimpleName();
    }
  }

  static class Child {
    @Advertise
    final GrandChild grandChild = new GrandChild();
  }

  static public class InfoProviderGrandChild {
    public String method(String label, GrandChild advertised, Object info) {
      return getClass().getSimpleName();
    }
  }

  @Advertise(infoProvider = InfoProviderGrandChild.class)
  static class GrandChild {
  }

  static public class InfoProviderMocString {
    public String method(String label, Object advertised, Object info) {
      return getClass().getSimpleName();
    }
  }

  @Advertise(infoProvider = InfoProviderMocString.class)
  protected final String mocString = "Hello";
  @Advertise(infoProvider = InfoProviderChild.class)
  protected final Child child = new Child();

  @Test
  public void testInvocationAdvertisedIsObjectWithFirstParent() {
    checkAdvertise(this, new String[] { InfoProviderChild.class.getSimpleName(),
                   InfoProviderGrandChild.class.getSimpleName(), InfoProviderMocString.class.getSimpleName() });
  }

  private void checkAdvertise(Object advertised, String[] expected) {
    final List<String> hasBeenAdvertised = new ArrayList<String>();
    Zephyr.advertisement().onAdvertise.connect(new Listener<Advertisement.Advertised>() {
      @Override
      public void listen(Advertised eventInfo) {
        if (!(eventInfo.info instanceof String))
          return;
        hasBeenAdvertised.add((String) eventInfo.info);
      }
    });
    Zephyr.advertise(null, advertised);
    String[] stringInfos = new String[hasBeenAdvertised.size()];
    hasBeenAdvertised.toArray(stringInfos);
    Assert.assertTrue(Arrays.equals(expected, stringInfos));
  }
}
