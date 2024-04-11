package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Patch;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;

public class StartRun extends Task {
  private final WorldPoint startPoint = new WorldPoint(1820, 3485, 0);
  @Inject private TitheFarm plugin;

  @Override
  public String getStatus() {
    return "Starting run";
  }

  @Override
  public boolean validate() {
    return !plugin.isStartedRun()
        && TitheFarm.isInTitheFarm()
        && Movement.getRunEnergy() >= 50
        && !Utils.instanceToWorld(Players.getLocal()).equals(startPoint);
  }

  @Override
  public void execute() {
    final WorldPoint instancePoint = Utils.worldToInstance(startPoint);
    if (instancePoint == null) {
      return;
    }

    Movement.walk(instancePoint);
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(instancePoint), 30);

    Patch.buildList();
    Patch.setBasePlantId();

    plugin.setStartedRun(true);
    plugin.setFinishedPlanting(false);

    if (!Movement.isRunEnabled()) {
      Movement.toggleRun();
    }
  }
}
