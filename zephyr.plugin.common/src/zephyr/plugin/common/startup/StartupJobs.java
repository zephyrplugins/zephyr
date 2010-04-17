package zephyr.plugin.common.startup;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;

public class StartupJobs extends Job {

  public StartupJobs() {
    super("Zephyr Starting Jobs");
  }

  @Override
  protected IStatus run(IProgressMonitor monitor) {
    List<StartupJob> jobs = buildJobList();
    monitor.beginTask(getName(), jobs.size());
    for (StartupJob job : jobs) {
      job.run();
      monitor.worked(1);
    }
    monitor.done();
    return Status.OK_STATUS;
  }

  private List<StartupJob> buildJobList() {
    IConfigurationElement[] config = Platform.getExtensionRegistry()
          .getConfigurationElementsFor("zephyr.plugin.common.startup");
    List<StartupJob> jobs = new ArrayList<StartupJob>();
    for (IConfigurationElement element : config) {
      Object o;
      try {
        o = element.createExecutableExtension("class");
        if (o instanceof StartupJob)
          jobs.add((StartupJob) o);
      } catch (CoreException e) {
        e.printStackTrace();
      }
    }
    Collections.sort(jobs, new Comparator<StartupJob>() {
      @Override
      public int compare(StartupJob o1, StartupJob o2) {
        return ((Integer) o1.level()).compareTo(o2.level());
      }
    });
    return jobs;
  }
}
