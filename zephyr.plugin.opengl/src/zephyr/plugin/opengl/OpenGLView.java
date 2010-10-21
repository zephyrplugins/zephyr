package zephyr.plugin.opengl;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;
import javax.media.opengl.GL2ES1;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawableFactory;
import javax.media.opengl.GLProfile;
import javax.media.opengl.fixedfunc.GLMatrixFunc;
import javax.media.opengl.glu.GLU;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ControlAdapter;
import org.eclipse.swt.events.ControlEvent;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.graphics.Rectangle;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.opengl.GLCanvas;
import org.eclipse.swt.opengl.GLData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.part.ViewPart;

import zephyr.plugin.core.views.SyncView;

abstract public class OpenGLView extends ViewPart implements SyncView {
  protected GLCanvas canvas;
  private GLProfile profile;
  private GLContext context;

  public OpenGLView() {
    profile = GLProfile.get(GLProfile.GL2);
  }

  @Override
  public void createPartControl(Composite parent) {
    parent.setLayout(new FillLayout());
    GLData data = new GLData();
    data.doubleBuffer = true;
    canvas = new GLCanvas(parent, SWT.NO_BACKGROUND, data);
    canvas.addControlListener(new ControlAdapter() {
      @Override
      public void controlResized(ControlEvent e) {
        resizeScene();
      }
    });
    canvas.addDisposeListener(new DisposeListener() {
      @Override
      public void widgetDisposed(DisposeEvent e) {
        dispose();
      }
    });
    profile = GLProfile.get(GLProfile.GL2);
    canvas.setCurrent();
    context = GLDrawableFactory.getFactory(profile).createExternalGLContext();
    Rectangle clientArea = parent.getClientArea();
    canvas.setSize(clientArea.width, clientArea.height);
    context.makeCurrent();
    GL2 gl = context.getGL().getGL2();
    gl.glClearColor(1.0f, 1.0f, 1.0f, 1.0f);
    gl.glColor3f(1.0f, 0.0f, 0.0f);
    gl.glHint(GL2ES1.GL_PERSPECTIVE_CORRECTION_HINT, GL.GL_NICEST);
    gl.glClearDepth(1.0);
    gl.glLineWidth(2);
    gl.glEnable(GL.GL_DEPTH_TEST);
    context.release();
  }

  protected void resizeScene() {
    Rectangle bounds = canvas.getBounds();
    float fAspect = (float) bounds.width / (float) bounds.height;
    canvas.setCurrent();
    context.makeCurrent();
    GL2 gl = context.getGL().getGL2();
    gl.glViewport(0, 0, bounds.width, bounds.height);
    gl.glMatrixMode(GLMatrixFunc.GL_PROJECTION);
    gl.glLoadIdentity();
    GLU glu = new GLU();
    glu.gluPerspective(45.0f, fAspect, 0.5f, 400.0f);
    gl.glMatrixMode(GLMatrixFunc.GL_MODELVIEW);
    gl.glLoadIdentity();
    context.release();
  }

  @Override
  public void setFocus() {
    canvas.setFocus();
  }

  @Override
  public void repaint() {
    canvas.setCurrent();
    context.makeCurrent();
    render(context.getGL().getGL2());
    canvas.swapBuffers();
    context.release();
  }

  abstract protected void render(GL2 gl);
}
