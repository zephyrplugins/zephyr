package zephyr.plugin.core.api.advertizement;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Stack;

import junit.framework.Assert;

import org.junit.Test;

import zephyr.plugin.core.api.Zephyr;
import zephyr.plugin.core.api.advertizement.Advertizement.Advertized;
import zephyr.plugin.core.api.signals.Listener;

public class AdvertizementParentTest {
  static public class ParentInfoProvider implements InfoProvider {
    @Override
    public Object provideInfo(String label, Object advertized, Stack<Object> parents, Object info) {
      return new ArrayList<Object>(parents);
    }
  }

  @Advertize(infoProvider = ParentInfoProvider.class)
  public static class Toto {
    String totoField = "TotoField";
    Tata tata = new Tata();

    @Override
    public String toString() {
      return "toto";
    }
  }

  @Advertize(infoProvider = ParentInfoProvider.class)
  public static class Tata {
    String tataField = "TataField";

    @Override
    public String toString() {
      return "tata";
    }
  }

  @Test
  public void testParent() {
    final List<Object> hasBeenAdvertized = new ArrayList<Object>();
    Zephyr.advertizement().onAdvertize.connect(new Listener<Advertizement.Advertized>() {
      @SuppressWarnings("unchecked")
      @Override
      public void listen(Advertized eventInfo) {
        if (!(eventInfo.info instanceof Collection<?>))
          return;
        for (Object element : (Collection<Object>) eventInfo.info)
          hasBeenAdvertized.add(element);
        hasBeenAdvertized.add(eventInfo.advertized);
      }
    });
    Toto toto = new Toto();
    Zephyr.advertize(null, toto);
    checkCollection(new Object[] { toto, toto, toto.tata, toto, toto.tata, toto.tata.tataField, toto, toto.totoField },
                    hasBeenAdvertized);
  }

  private void checkCollection(Object[] expected, List<Object> collection) {
    Assert.assertEquals(expected.length, collection.size());
    for (int i = 0; i < expected.length; i++)
      Assert.assertEquals(expected[i], collection.get(i));
  }
}
