/*
 * CritterViz.java
 *
 * Created on March 12, 2009, 10:37 AM
 */

package zephyr.plugin.critterview.views;

import java.awt.Frame;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JPanel;

import critterbot.CritterbotProblem;
import critterbot.environment.CritterbotDrops;

public class CritterViz extends JPanel {
  private static final long serialVersionUID = 1L;
  private javax.swing.JLabel jLabel1;
  private javax.swing.JLabel voltageLabel;
  private javax.swing.JLabel looptimeLabel;
  private javax.swing.JPanel jPanel1;
  private javax.swing.JPanel jPanel11;
  private javax.swing.JPanel jPanel12;
  private javax.swing.JPanel jPanel2;
  private javax.swing.JPanel jPanel8;
  private javax.swing.JPanel jPanel9;
  private javax.swing.JPanel jPanelIR;
  private javax.swing.JPanel jPanelInertial;
  private javax.swing.JPanel jPanelLight;
  private javax.swing.JPanel jPanelMotor;
  private javax.swing.JPanel jPanelInfo;
  private javax.swing.JTextField voltage;
  private javax.swing.JTextField looptime;
  private final List<RichDisplayBar> bars = new ArrayList<RichDisplayBar>();
  private int busVoltageIndex = -1;
  private final Frame frame;

  public CritterViz(Frame frame, CritterbotProblem problem) {
    busVoltageIndex = labelIndex(problem, CritterbotDrops.BusVoltage);
    initComponents(problem);
    this.frame = frame;
    frame.add(this);
  }

  private void initComponents(CritterbotProblem problem) {
    java.awt.GridBagConstraints gridBagConstraints;

    jLabel1 = new javax.swing.JLabel();
    jPanel1 = new javax.swing.JPanel();
    jPanelIR = new javax.swing.JPanel();
    RichDisplayBar ir0 = newRichDisplayBar(problem, "IRDistance0");
    RichDisplayBar ir1 = newRichDisplayBar(problem, "IRDistance1");
    RichDisplayBar ir2 = newRichDisplayBar(problem, "IRDistance2");
    RichDisplayBar ir3 = newRichDisplayBar(problem, "IRDistance3");
    RichDisplayBar ir4 = newRichDisplayBar(problem, "IRDistance4");
    RichDisplayBar ir5 = newRichDisplayBar(problem, "IRDistance5");
    RichDisplayBar ir6 = newRichDisplayBar(problem, "IRDistance6");
    RichDisplayBar ir7 = newRichDisplayBar(problem, "IRDistance7");
    RichDisplayBar ir8 = newRichDisplayBar(problem, "IRDistance8");
    RichDisplayBar ir9 = newRichDisplayBar(problem, "IRDistance9");
    jPanelLight = new javax.swing.JPanel();
    RichDisplayBar light0 = newRichDisplayBar(problem, "Light0");
    RichDisplayBar light1 = newRichDisplayBar(problem, "Light1");
    RichDisplayBar light2 = newRichDisplayBar(problem, "Light2");
    RichDisplayBar light3 = newRichDisplayBar(problem, "Light3");
    jPanelInfo = new javax.swing.JPanel();
    voltageLabel = new javax.swing.JLabel();
    voltage = new javax.swing.JTextField();
    looptimeLabel = new javax.swing.JLabel();
    looptime = new javax.swing.JTextField();
    jPanelMotor = new javax.swing.JPanel();
    jPanel2 = new javax.swing.JPanel();
    RichDisplayBar mspeed0 = newRichDisplayBar(problem, "Motor0Speed");
    RichDisplayBar mspeed1 = newRichDisplayBar(problem, "Motor1Speed");
    RichDisplayBar mspeed2 = newRichDisplayBar(problem, "Motor2Speed");
    jPanel8 = new javax.swing.JPanel();
    RichDisplayBar mcurrent0 = newRichDisplayBar(problem, "Motor0Current");
    RichDisplayBar mcurrent1 = newRichDisplayBar(problem, "Motor1Current");
    RichDisplayBar mcurrent2 = newRichDisplayBar(problem, "Motor2Current");
    jPanel9 = new javax.swing.JPanel();
    RichDisplayBar mtemp0 = newRichDisplayBar(problem, "Motor0Temperature", 160);
    RichDisplayBar mtemp1 = newRichDisplayBar(problem, "Motor1Temperature", 160);
    RichDisplayBar mtemp2 = newRichDisplayBar(problem, "Motor2Temperature", 160);
    jPanelInertial = new javax.swing.JPanel();
    jPanel11 = new javax.swing.JPanel();
    RichDisplayBar rotation = newRichDisplayBar(problem, "RotationVel");
    jPanel12 = new javax.swing.JPanel();
    RichDisplayBar accelx = newRichDisplayBar(problem, "AccelX");
    RichDisplayBar accely = newRichDisplayBar(problem, "AccelY");
    RichDisplayBar accelz = newRichDisplayBar(problem, "AccelZ");

    jLabel1.setText("jLabel1");

    setLayout(new java.awt.CardLayout());

    jPanel1.setBorder(javax.swing.BorderFactory.createEmptyBorder());
    jPanel1.setLayout(new java.awt.GridBagLayout());

    jPanelIR.setBorder(javax.swing.BorderFactory.createTitledBorder("IR Distance Sensors"));
    jPanelIR.setLayout(new java.awt.GridLayout(1, 0));

    ir0.setRange(0, 255);
    ir0.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir0);

    ir1.setRange(0, 255);
    ir1.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir1);

    ir2.setRange(0, 255);
    ir2.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir2);

    ir3.setRange(0, 255);
    ir3.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir3);

    ir4.setRange(0, 255);
    ir4.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir4);

    ir5.setRange(0, 255);
    ir5.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir5);

    ir6.setRange(0, 255);
    ir6.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir6);

    ir7.setRange(0, 255);
    ir7.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir7);

    ir8.setRange(0, 255);
    ir8.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir8);

    ir9.setRange(0, 255);
    ir9.setLayout(new java.awt.GridLayout(1, 0));
    jPanelIR.add(ir9);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.gridheight = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 6.0;
    gridBagConstraints.weighty = 6.0;
    jPanel1.add(jPanelIR, gridBagConstraints);

    jPanelLight.setBorder(javax.swing.BorderFactory.createTitledBorder("Light Sensors"));
    jPanelLight.setLayout(new java.awt.GridLayout(1, 0));

    light0.setRange(0, 800);
    light0.setLayout(new java.awt.GridLayout(1, 0));
    jPanelLight.add(light0);

    light1.setRange(0, 800);
    light1.setLayout(new java.awt.GridLayout(1, 0));
    jPanelLight.add(light1);

    light2.setRange(0, 800);
    light2.setLayout(new java.awt.GridLayout(1, 0));
    jPanelLight.add(light2);

    light3.setRange(0, 800);
    light3.setLayout(new java.awt.GridLayout(1, 0));
    jPanelLight.add(light3);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 1;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    jPanel1.add(jPanelLight, gridBagConstraints);

    jPanelInfo.setBorder(javax.swing.BorderFactory.createTitledBorder("Info"));

    voltageLabel.setText("Voltage:");
    jPanelInfo.add(voltageLabel);
    voltage.setColumns(4);
    voltage.setEditable(false);
    voltage.setText("0.00");
    jPanelInfo.add(voltage);

    looptimeLabel.setText("Loop time:");
    jPanelInfo.add(looptimeLabel);
    looptime.setColumns(4);
    looptime.setEditable(false);
    looptime.setText("0ms");
    jPanelInfo.add(looptime);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    jPanel1.add(jPanelInfo, gridBagConstraints);

    jPanelMotor.setBorder(javax.swing.BorderFactory.createTitledBorder("Motors"));
    jPanelMotor.setLayout(new java.awt.GridLayout(1, 0));

    jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("Speed"));
    jPanel2.setLayout(new java.awt.GridLayout(1, 0));

    mspeed0.setRange(-35, 35);
    mspeed0.setLayout(new java.awt.GridLayout(1, 0));
    jPanel2.add(mspeed0);

    mspeed1.setRange(-35, 35);
    mspeed1.setLayout(new java.awt.GridLayout(1, 0));
    jPanel2.add(mspeed1);

    mspeed2.setRange(-35, 35);
    mspeed2.setLayout(new java.awt.GridLayout(1, 0));
    jPanel2.add(mspeed2);

    jPanelMotor.add(jPanel2);

    jPanel8.setBorder(javax.swing.BorderFactory.createTitledBorder("Current"));
    jPanel8.setLayout(new java.awt.GridLayout(1, 0));

    mcurrent0.setRange(0, 90);
    mcurrent0.setLayout(new java.awt.GridLayout(1, 0));
    jPanel8.add(mcurrent0);

    mcurrent1.setRange(0, 90);
    mcurrent1.setLayout(new java.awt.GridLayout(1, 0));
    jPanel8.add(mcurrent1);

    mcurrent2.setRange(0, 90);
    mcurrent2.setLayout(new java.awt.GridLayout(1, 0));
    jPanel8.add(mcurrent2);

    jPanelMotor.add(jPanel8);

    jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("Temperature"));
    jPanel9.setLayout(new java.awt.GridLayout(1, 0));

    mtemp0.setRange(40, 175);
    mtemp0.setLayout(new java.awt.GridLayout(1, 0));
    jPanel9.add(mtemp0);

    mtemp1.setRange(40, 175);
    mtemp1.setLayout(new java.awt.GridLayout(1, 0));
    jPanel9.add(mtemp1);

    mtemp2.setRange(40, 175);
    mtemp2.setLayout(new java.awt.GridLayout(1, 0));
    jPanel9.add(mtemp2);

    jPanelMotor.add(jPanel9);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 6.0;
    jPanel1.add(jPanelMotor, gridBagConstraints);

    jPanelInertial.setBorder(javax.swing.BorderFactory.createTitledBorder("Inertial Sensors"));
    jPanelInertial.setLayout(new java.awt.GridBagLayout());

    jPanel11.setBorder(javax.swing.BorderFactory.createTitledBorder("Gyroscope"));
    jPanel11.setLayout(new java.awt.GridLayout(1, 0));

    rotation.setRange(-550, 550);
    rotation.setLayout(new java.awt.GridLayout(1, 0));
    jPanel11.add(rotation);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 0;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.0;
    gridBagConstraints.weighty = 1.0;
    jPanelInertial.add(jPanel11, gridBagConstraints);

    jPanel12.setBorder(javax.swing.BorderFactory.createTitledBorder("Accelerometer"));
    jPanel12.setLayout(new java.awt.GridLayout(1, 0));

    accelx.setRange(-2048, 2048);
    accelx.setLayout(new java.awt.GridLayout(1, 0));
    jPanel12.add(accelx);

    accely.setRange(-2048, 2048);
    accely.setLayout(new java.awt.GridLayout(1, 0));
    jPanel12.add(accely);

    accelz.setRange(-2048, 2048);
    accelz.setLayout(new java.awt.GridLayout(1, 0));
    jPanel12.add(accelz);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 0;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 1.5;
    gridBagConstraints.weighty = 1.0;
    jPanelInertial.add(jPanel12, gridBagConstraints);

    gridBagConstraints = new java.awt.GridBagConstraints();
    gridBagConstraints.gridx = 1;
    gridBagConstraints.gridy = 2;
    gridBagConstraints.fill = java.awt.GridBagConstraints.BOTH;
    gridBagConstraints.weightx = 0.7;
    jPanel1.add(jPanelInertial, gridBagConstraints);

    add(jPanel1, "card2");
  }


  private RichDisplayBar newRichDisplayBar(CritterbotProblem problem, String label) {
    return newRichDisplayBar(problem, label, 0.0);
  }

  private RichDisplayBar newRichDisplayBar(CritterbotProblem problem, String label, double defaultValue) {
    int observationIndex = labelIndex(problem, label);
    RichDisplayBar bar = new RichDisplayBar(observationIndex, defaultValue);
    bars.add(bar);
    return bar;
  }

  private int labelIndex(CritterbotProblem problem, String label) {
    int observationIndex = -1;
    if (problem.legend().hasLabel(label))
      observationIndex = problem.legend().indexOf(label);
    else
      System.err.println("Warning: " + label + " not found in legend");
    return observationIndex;
  }

  public void updateDisplay(double period, double[] currentObservation) {
    for (RichDisplayBar bar : bars)
      bar.updateValue(currentObservation);
    if (busVoltageIndex >= 0)
      voltage.setText(String.format("%.1f", currentObservation[busVoltageIndex] / 10.0));
    looptime.setText(String.format("%.1fms", period));
  }

  public void dispose() {
    frame.remove(this);
    removeAll();
  }
}
