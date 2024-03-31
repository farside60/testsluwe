package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.tasks.RelogTask;
import javax.inject.Inject;

public class Relog extends RelogTask {
  @Inject private Tempoross plugin;

  @Override
  public boolean validate() {
    return plugin.isRelog() && plugin.isInDesert();
  }

  @Override
  public void execute() {
    plugin.setRelog(false);
    super.execute();
  }
}
