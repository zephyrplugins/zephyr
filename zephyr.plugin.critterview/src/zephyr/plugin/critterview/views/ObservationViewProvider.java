package zephyr.plugin.critterview.views;

import zephyr.plugin.common.helpers.ClassViewProvider;
import critterbot.CritterbotProblem;

public class ObservationViewProvider extends ClassViewProvider {
  public ObservationViewProvider() {
    super(CritterbotProblem.class, ObservationView.ID);
  }
}
