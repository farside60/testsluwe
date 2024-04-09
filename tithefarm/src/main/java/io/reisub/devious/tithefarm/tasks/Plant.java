package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Patch;
import io.reisub.devious.tithefarm.PatchState;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class Plant extends Task {
  @Inject private TitheFarm plugin;
  private Patch currentPatch;

  @Override
  public String getStatus() {
    return "Planting";
  }

  @Override
  public boolean validate() {
    if (!plugin.isStartedRun() || !TitheFarm.isInTitheFarm()) {
      return false;
    }

    currentPatch = Patch.getCurrent();

    return currentPatch != null && currentPatch.getState() == PatchState.EMPTY;
  }

  @Override
  public void execute() {
    final Item seed = Inventory.getFirst(Predicates.ids(TitheFarm.SEED_IDS));
    if (seed == null) {
      return;
    }

    seed.useOn(currentPatch.getObject());
    Time.sleepTicks(2);
  }
}
