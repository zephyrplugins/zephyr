package zephyr.plugin.core.observations;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import zephyr.plugin.core.internal.observations.LineData;

public class SensorTextGroup implements ObsWidget {
  static public abstract class TextClient {
    public final String label;

    public TextClient(String label) {
      this.label = label;
    }

    public abstract String currentText();
  }

  private final String title;
  private final TextClient[] textClients;
  private final List<Label> labels;
  private final String[] texts;

  public SensorTextGroup(String title, TextClient... textClients) {
    this.title = title;
    this.textClients = textClients;
    labels = new ArrayList<Label>();
    texts = new String[textClients.length];
  }

  @Override
  synchronized public void createWidgetComposite(Composite parent) {
    if (!hasContent())
      return;
    Group group = new Group(parent, SWT.NONE);
    group.setText(title);
    group.setLayoutData(new LineData(false));
    GridLayout gridLayout = new GridLayout();
    gridLayout.numColumns = 2;
    group.setLayout(gridLayout);
    for (int i = 0; i < textClients.length; i++) {
      TextClient textClient = textClients[i];
      createLabels(group, textClient);
    }
  }

  private void createLabels(Group group, TextClient textClient) {
    Label staticLabel = new Label(group, SWT.NONE);
    staticLabel.setText(textClient.label);
    staticLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));

    Label dynLabel = new Label(group, SWT.RIGHT);
    labels.add(dynLabel);
    dynLabel.setText(textClient.currentText());
    dynLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
  }

  @Override
  synchronized public void updateValue(double[] currentObservation) {
    for (int i = 0; i < textClients.length; i++)
      texts[i] = textClients[i].currentText();
  }

  @Override
  public void repaint() {
    for (int i = 0; i < texts.length; i++) {
      Label label = labels.get(i);
      label.setText(texts[i]);
      label.redraw();
    }
  }

  @Override
  public boolean hasContent() {
    return texts.length > 0;
  }
}
