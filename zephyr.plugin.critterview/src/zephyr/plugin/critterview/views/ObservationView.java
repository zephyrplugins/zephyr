package zephyr.plugin.critterview.views;

import static critterbot.environment.CritterbotDrops.Accel;
import static critterbot.environment.CritterbotDrops.BusVoltage;
import static critterbot.environment.CritterbotDrops.IRDistance;
import static critterbot.environment.CritterbotDrops.Light;
import static critterbot.environment.CritterbotDrops.Motor;
import static critterbot.environment.CritterbotDrops.MotorCurrent;
import static critterbot.environment.CritterbotDrops.MotorSpeed;
import static critterbot.environment.CritterbotDrops.MotorTemperature;
import static critterbot.environment.CritterbotDrops.RotationVel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.observations.EnvironmentView;
import zephyr.plugin.core.observations.ObsLayout;
import zephyr.plugin.core.observations.ObsWidget;
import zephyr.plugin.core.observations.SensorCollection;
import zephyr.plugin.core.observations.SensorGroup;
import zephyr.plugin.core.observations.SensorTextGroup;
import zephyr.plugin.core.observations.SensorTextGroup.TextClient;
import critterbot.CritterbotProblem;

public class ObservationView extends EnvironmentView {
  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(CritterbotProblem.class, ObservationView.ID);
    }
  }

  public static final String ID = "zephyr.plugin.critterview.view.observation";
  protected CritterbotProblem environment = null;
  double[] currentObservation;

  public ObservationView() {
  }

  @Override
  protected ObsLayout getObservationLayout() {
    SensorGroup irDistanceGroup = new SensorGroup("IR Distance Sensors", startsWith(IRDistance), 0, 255);
    SensorGroup lightGroup = new SensorGroup("Light Sensors", startsWith(Light), 0, 800);
    SensorGroup motorSpeedGroup = new SensorGroup("Speed", patternWith(Motor, MotorSpeed), -35, 35);
    SensorGroup motorCurrentGroup = new SensorGroup("Current", patternWith(Motor, MotorCurrent), 0, 90);
    SensorGroup motorTemperatureGroup = new SensorGroup("Temperature", patternWith(Motor, MotorTemperature), 40, 175);
    SensorCollection motorCollection = new SensorCollection("Motors", motorSpeedGroup, motorCurrentGroup,
                                                            motorTemperatureGroup);
    SensorGroup rotVelGroup = new SensorGroup("Gyroscope", startsWith(RotationVel), 0, 255);
    SensorGroup accelGroup = new SensorGroup("Accelerometer", startsWith(Accel), -2048, 2048);
    SensorCollection inertialCollection = new SensorCollection("Inertial Sensors", rotVelGroup, accelGroup);
    SensorTextGroup infoGroup = createInfoGroup();
    return new ObsLayout(new ObsWidget[][] { { infoGroup, irDistanceGroup, lightGroup },
                                             { motorCollection, inertialCollection } });
  }

  private SensorTextGroup createInfoGroup() {
    return new SensorTextGroup("Info", new TextClient("Voltage:") {
      int busVoltageIndex = environment.legend().indexOf(BusVoltage);

      @Override
      public String currentText() {
        if (currentObservation == null)
          return "00.0V";
        return String.format("%.1fV", currentObservation[busVoltageIndex] / 10.0);
      }
    }, new TextClient("Loop Time:") {
      @Override
      public String currentText() {
        if (environment == null)
          return "00.0ms";
        return String.valueOf(String.format("%.1fms", environment.clock().period()));
      }
    });
  }

  private List<Integer> patternWith(String prefix, String suffix) {
    List<Integer> result = new ArrayList<Integer>();
    for (Map.Entry<String, Integer> entry : environment.legend().legend().entrySet()) {
      if (entry.getKey().startsWith(prefix) && entry.getKey().endsWith(suffix))
        result.add(entry.getValue());
    }
    Collections.sort(result);
    return result;
  }

  private List<Integer> startsWith(String prefix) {
    List<Integer> result = new ArrayList<Integer>();
    for (Map.Entry<String, Integer> entry : environment.legend().legend().entrySet()) {
      if (entry.getKey().startsWith(prefix))
        result.add(entry.getValue());
    }
    Collections.sort(result);
    return result;
  }

  @Override
  public boolean synchronize() {
    currentObservation = environment.currentStep().o_tp1;
    return synchronize(currentObservation);
  }

  @Override
  public void addTimed(String info, Object drawn) {
    environment = (CritterbotProblem) drawn;
    createLayout();
  }

  @Override
  public boolean canTimedAdded() {
    return environment == null;
  }
}
