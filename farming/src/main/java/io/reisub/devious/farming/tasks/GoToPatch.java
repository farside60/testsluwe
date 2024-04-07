package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Config;
import io.reisub.devious.farming.Farming;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;

public class GoToPatch extends Task {
  @Inject private Farming plugin;

  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to " + plugin.getCurrentLocation().getName() + " patch";
  }

  @Override
  public boolean validate() {
    return plugin.getCurrentLocation() != null
        && !Utils.isInRegion(plugin.getCurrentLocation().getRegionId());
  }

  @Override
  public void execute() {
    WorldPoint current = Players.getLocal().getWorldLocation();

    if (plugin.getCurrentLocation().getTeleportable().teleport()) {
      Time.sleepTicksUntil(
          () ->
              Players.getLocal().getWorldLocation() != null
                  && !Players.getLocal().getWorldLocation().equals(current),
          10);
    }

    if (Players.getLocal().distanceTo(plugin.getCurrentLocation().getPatchPoint()) > 10) {
      SluweMovement.walkTo(plugin.getCurrentLocation().getPatchPoint());
    }
  }
}
