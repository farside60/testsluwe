package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Patch;
import io.reisub.devious.tithefarm.PatchState;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;

public class Harvest extends Task {
  @Inject private TitheFarm plugin;
  private Patch currentPatch;

  @Override
  public String getStatus() {
    return "Harvesting";
  }

  @Override
  public boolean validate() {
    if (!plugin.isStartedRun() || !TitheFarm.isInTitheFarm()) {
      return false;
    }

    currentPatch = Patch.getCurrent();

    return currentPatch != null && currentPatch.getState() == PatchState.GROWN;
  }

  @Override
  public void execute() {
    final TileObject plant = currentPatch.getObject();
    if (plant == null || !plant.hasAction("Harvest")) {
      return;
    }

    plant.interact("Harvest");
    Time.sleepTicks(3);

    if (Patch.isAtEnd()) {
      plugin.setStartedRun(false);
    } else {
      Patch.takeStep();
    }
  }
}
