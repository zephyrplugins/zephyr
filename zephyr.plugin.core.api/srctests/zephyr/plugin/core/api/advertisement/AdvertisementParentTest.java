package zephyr.plugin.core.api.advertisement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertisement.Advertisement.Advertised;
import zephyr.plugin.core.api.signals.Listener;
import zephyr.plugin.core.api.synchronization.Clock;

public class AdvertisementParentTest {
  static public class ParentInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String label, Object advertised, Stack<Object> parents, Object info) {
      return new ArrayList<Object>(parents);
    }
  }

  @Advertise(infoProvider = ParentInfoProvider.class)
  public static class Toto {
    final public String totoField = "TotoField";
    final Tata tata = new Tata();

    @Override
    public String toString() {
      return "toto";
    }
  }

  @Advertise(infoProvider = ParentInfoProvider.class)
  public static class Tata {
    final public String tataField = "TataField";

    @Override
    public String toString() {
      return "tata";
    }
  }

  @Test
  public void testParent() {
    final List<Object> hasBeenAdvertised = new ArrayList<Object>();
    Zephyr.advertisement().onAdvertiseNode.connect(new Listener<Advertisement.Advertised>() {
      @SuppressWarnings("unchecked")
      @Override
      public void listen(Advertised eventInfo) {
        if (!(eventInfo.info instanceof Collection<?>))
          return;
        for (Object element : (Collection<Object>) eventInfo.info)
          hasBeenAdvertised.add(element);
        hasBeenAdvertised.add(eventInfo.advertised);
      }
    });
    Toto toto = new Toto();
    Zephyr.advertise((Clock) null, toto);
    checkCollection(new Object[] { toto, toto, toto.tata, toto, toto.tata, toto.tata.tataField, toto, toto.totoField },
                    hasBeenAdvertised);
  }

  private void checkCollection(Object[] expected, List<Object> collection) {
    Assert.assertEquals(expected.length, collection.size());
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], collection.get(i));
  }
}
