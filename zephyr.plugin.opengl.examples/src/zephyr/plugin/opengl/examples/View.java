package zephyr.plugin.opengl.examples;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import zephyr.plugin.core.api.codeparser.codetree.ClassNode;
import zephyr.plugin.core.api.codeparser.interfaces.CodeNode;
import zephyr.plugin.core.api.synchronization.Clock;
import zephyr.plugin.core.helpers.ClassViewProvider;
import zephyr.plugin.core.views.TimedView;
import zephyr.plugin.opengl.OpenGLView;

/*
 * Adapted from Snippet209
 * SWT OpenGL snippet: use JOGL to draw to an SWT GLCanvas
 *
 */
public class View extends OpenGLView implements TimedView {
  static public class Provider extends ClassViewProvider {
    public Provider() {
      super(Model.class);
    }
  }

  private Model model;
  private long time;
  private float insideRadius;

  @Override
  public boolean synchronize(Clock clock) {
    time = model.clock.timeStep();
    insideRadius = model.insideRadius;
    return true;
  }

  @Override
  protected void render(GL2 gl) {
    gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
    gl.glClearColor(.3f, .5f, .8f, 1.0f);
    gl.glLoadIdentity();
    gl.glTranslatef(0.0f, 0.0f, -10.0f);
    float frot = time;
    gl.glRotatef(0.15f * time, 2.0f * frot, 10.0f * frot, 1.0f);
    gl.glRotatef(0.3f * time, 3.0f * frot, 1.0f * frot, 1.0f);
    gl.glPolygonMode(GL.GL_FRONT_AND_BACK, GL2GL3.GL_LINE);
    gl.glColor3f(0.9f, 0.9f, 0.9f);
    drawTorus(gl, 1, insideRadius, 15, 15);
  }

  static void drawTorus(GL2 gl, float r, float R, int nsides, int rings) {
    float ringDelta = 2.0f * (float) Math.PI / rings;
    float sideDelta = 2.0f * (float) Math.PI / nsides;
    float theta = 0.0f, cosTheta = 1.0f, sinTheta = 0.0f;
    for (int i = rings - 1; i >= 0; i--) {
      float theta1 = theta + ringDelta;
      float cosTheta1 = (float) Math.cos(theta1);
      float sinTheta1 = (float) Math.sin(theta1);
      gl.glBegin(GL2.GL_QUAD_STRIP);
      float phi = 0.0f;
      for (int j = nsides; j >= 0; j--) {
        phi += sideDelta;
        float cosPhi = (float) Math.cos(phi);
        float sinPhi = (float) Math.sin(phi);
        float dist = R + r * cosPhi;
        gl.glNormal3f(cosTheta1 * cosPhi, -sinTheta1 * cosPhi, sinPhi);
        gl.glVertex3f(cosTheta1 * dist, -sinTheta1 * dist, r * sinPhi);
        gl.glNormal3f(cosTheta * cosPhi, -sinTheta * cosPhi, sinPhi);
        gl.glVertex3f(cosTheta * dist, -sinTheta * dist, r * sinPhi);
      }
      gl.glEnd();
      theta = theta1;
      cosTheta = cosTheta1;
      sinTheta = sinTheta1;
    }
  }

  @Override
  public boolean[] provide(CodeNode[] codeNode) {
    if (model != null)
      return new boolean[] { false };
    model = (Model) ((ClassNode) codeNode[0]).instance();
    return new boolean[] { true };
  }

  @Override
  public void removeClock(Clock clock) {
    dispose();
  }
}
