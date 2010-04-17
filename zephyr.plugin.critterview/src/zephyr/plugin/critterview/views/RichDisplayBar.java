package zephyr.plugin.critterview.views;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;

import javax.swing.JComponent;

/**
 * 
 * @author mvs
 */
public class RichDisplayBar extends JComponent {
  private static final long serialVersionUID = 1L;

  private static final int V_MARGIN = 4;
  private static final int H_MARGIN = 2;
  private static final int BAR_WIDTH = 15;

  private int dispMin, dispMax, dispHeight;
  private final boolean fixedRange;
  private double rangeMin, rangeMax;
  private double min, max, avg, current, stdDev;
  private double decayMax, decayMin;
  private double stdCalc;
  private long numSamples;
  private final int observationIndex;

  public RichDisplayBar(int observationIndex, double defVal) {
    this.observationIndex = observationIndex;
    this.setSize(new Dimension(25, 300));
    dispMin = dispMax = dispHeight = 0;
    stdDev = 0;
    current = min = max = avg = defVal;
    decayMax = decayMin = defVal;
    numSamples = 0;
    stdCalc = 0;
    rangeMin = -1;
    rangeMax = 1;
    fixedRange = true;
  }

  @Override
  public void paint(Graphics g) {
    super.paint(g);
    if (observationIndex < 0)
      return;

    dispMin = V_MARGIN + 1;
    dispMax = getHeight() - V_MARGIN - 2;
    dispHeight = dispMax - dispMin;

    // Draw background
    g.setColor(Color.white);
    g.fillRect(H_MARGIN, dispMin - 1, BAR_WIDTH, dispHeight + 2);

    // Draw Standard Deviation
    g.setColor(Color.pink);
    int stdHigh = dispMax - (int) ((avg - stdDev - rangeMin) * getScale());
    if (stdHigh > dispHeight + 6)
      stdHigh = dispHeight + 6;
    int stdLow = dispMax - (int) ((avg + stdDev - rangeMin) * getScale());
    g.fillRect(H_MARGIN, stdLow, BAR_WIDTH, stdHigh - stdLow);

    // Draw Average
    g.setColor(Color.black);
    g.drawRect(H_MARGIN, dispMax - (int) ((avg - rangeMin) * getScale()),
               BAR_WIDTH, 1);

    // Draw bar border
    g.setColor(Color.lightGray);
    g.drawRect(H_MARGIN, dispMin - 1, BAR_WIDTH, dispHeight + 2);


    // Draw min/max marks
    int dMinTick = dispMax - (int) ((decayMin - rangeMin) * getScale());
    int dMaxTick = dispMax - (int) ((decayMax - rangeMin) * getScale());
    int minTick = dispMax - (int) ((min - rangeMin) * getScale());
    int maxTick = dispMax - (int) ((max - rangeMin) * getScale());
    g.setColor(Color.black);
    g.fillPolygon(new int[] { H_MARGIN + BAR_WIDTH + 1,
        H_MARGIN + BAR_WIDTH + 5,
        H_MARGIN + BAR_WIDTH + 5 },
                  new int[] { dMinTick, dMinTick - 4, dMinTick + 4 }, 3);
    g.fillPolygon(new int[] { H_MARGIN + BAR_WIDTH + 1,
        H_MARGIN + BAR_WIDTH + 5,
        H_MARGIN + BAR_WIDTH + 5 },
                  new int[] { dMaxTick, dMaxTick + 4, dMaxTick - 4 }, 3);
    g.setColor(Color.lightGray);
    g.fillRect(H_MARGIN, dispMin - 1, BAR_WIDTH, maxTick - dispMin);
    g.fillRect(H_MARGIN, minTick + 2, BAR_WIDTH, dispHeight + 5 - minTick);

    // Draw Current Value
    g.setColor(Color.red);
    int currentTick = dispMax - (int) ((current - rangeMin) * getScale());
    // g.drawLine(H_MARGIN + 1, currentTick, H_MARGIN + BAR_WIDTH, currentTick);
    g.fillRect(H_MARGIN, currentTick - 1, BAR_WIDTH, 3);
    g.fillPolygon(new int[] { H_MARGIN + BAR_WIDTH + 1,
        H_MARGIN + BAR_WIDTH + 5,
        H_MARGIN + BAR_WIDTH + 5 },
                  new int[] { currentTick, currentTick + 4, currentTick - 4 },
                  3);
  }

  public void updateValue(double[] currentObservation) {
    if (observationIndex < 0)
      return;
    double newVal = currentObservation[observationIndex];
    if (numSamples == 0) {
      min = max = newVal;
      decayMax = decayMin = newVal;
    }
    stdCalc += numSamples * Math.pow(newVal - avg, 2) /
        (numSamples + 1);
    numSamples++;
    avg += (newVal - avg) / numSamples;
    stdDev = Math.sqrt(stdCalc / (numSamples + 1));

    decayMax -= (max - min) / 10000;
    decayMin += (max - min) / 10000;

    if (newVal > decayMax)
      decayMax = newVal;
    if (newVal < decayMin)
      decayMin = newVal;

    if (newVal > max)
      max = newVal;
    if (newVal < min)
      min = newVal;

    if (!fixedRange) {
      if (max > rangeMax)
        rangeMax = max;
      if (min < rangeMin)
        rangeMin = min;
    }

    current = newVal;
  }

  private double getScale() {
    return dispHeight / (rangeMax - rangeMin);
  }

  public void setRange(double newMin, double newMax) {
    if (newMin > newMax) {
      rangeMin = newMin;
      rangeMax = newMax;
      return;
    }
    rangeMin = newMin;
    rangeMax = newMax;
  }

  public void setMin(double newMin) {
    if (newMin > max)
      max = newMin;
    min = newMin;
  }

  public void setmax(double newMax) {
    if (newMax < min)
      min = newMax;
    max = newMax;
  }

}
