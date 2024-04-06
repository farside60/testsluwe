package io.reisub.devious.herblore.tasks;

import io.reisub.devious.herblore.Herblore;
import io.reisub.devious.herblore.HerbloreTask;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.items.Inventory;

public class Clean extends Task {
  @Inject private Herblore plugin;

  @Override
  public String getStatus() {
    return "Cleaning herbs";
  }

  @Override
  public boolean validate() {
    HerbloreTask task = plugin.getConfig().task();

    return (task == HerbloreTask.CLEAN_HERBS
            || task == HerbloreTask.MAKE_UNFINISHED
            || task == HerbloreTask.MAKE_POTION
            || task == HerbloreTask.TAR_HERBS)
        && plugin.isCurrentActivity(Activity.IDLE)
        && Inventory.contains(plugin.getGrimyHerbIds());
  }

  @Override
  public void execute() {
    plugin.setActivity(Herblore.CLEANING_HERBS);
    Inventory.getAll(plugin.getGrimyHerbIds()).forEach((i) -> i.interact("Clean"));
  }
}
