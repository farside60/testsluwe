package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.Config;
import io.reisub.devious.tutorialisland.Ironman;
import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;

public class StopPlugin extends Task {
  @Inject private TutorialIsland plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Stopping plugin";
  }

  @Override
  public boolean validate() {
    if (TutorialIsland.isProgress(670) && config.dontLeave()) {
      return config.ironman() == Ironman.NONE || plugin.isIronman();
    }

    if (plugin.isIronman()
        && (config.ironman() == Ironman.GROUP || config.ironman() == Ironman.GROUP_HARDCORE)) {
      return true;
    }

    return Utils.isInRegion(12850);
  }

  @Override
  public void execute() {
    if (Utils.isInRegion(12850)) {
      plugin.stop("We're in Lumbridge! Stopping plugin.");
    } else if (plugin.isIronman()
        && (config.ironman() == Ironman.GROUP || config.ironman() == Ironman.GROUP_HARDCORE)) {
      plugin.stop("You're a group ironman! Teaming up is your job. Stopping plugin.");
    } else {
      plugin.stop(
          "We've finished the tutorial but we're still on the island, as requested. "
              + "Stopping plugin.");
    }
  }
}
