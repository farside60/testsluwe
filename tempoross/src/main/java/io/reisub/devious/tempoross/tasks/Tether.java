package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.api.interaction.checks.CurrentActivityCheck;
import io.reisub.devious.utils.api.interaction.checks.IdleActivityCheck;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NullObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class Tether extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Tethering";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea()) {
      return false;
    }

    return plugin.isWaveIncoming()
        && !plugin.isCurrentActivity(Tempoross.TETHERING_MAST)
        && !plugin.isCurrentActivity(Tempoross.REPAIRING);
  }

  @Override
  public void execute() {
    final TileObject tetherObject =
        TileObjects.getNearest(
            NullObjectID.NULL_41352,
            NullObjectID.NULL_41353,
            NullObjectID.NULL_41354,
            NullObjectID.NULL_41355);
    if (tetherObject == null) {
      return;
    }

    if (!plugin.isCurrentActivity(Activity.IDLE)
        && !plugin.wasPreviousActivity(Tempoross.REPAIRING)) {
      int waitTicks = 10 - (Players.getLocal().distanceTo(tetherObject) / 2);
      Time.sleepTicksUntil(() -> plugin.isCurrentActivity(Activity.IDLE), waitTicks);
    }

    new Interaction(
            tetherObject,
            new CurrentActivityCheck(20, plugin, Tempoross.TETHERING_MAST),
            new IdleActivityCheck(35, plugin))
        .interact();
  }
}
