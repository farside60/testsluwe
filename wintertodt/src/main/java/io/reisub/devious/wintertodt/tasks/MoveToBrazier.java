package io.reisub.devious.wintertodt.tasks;

import com.google.inject.Inject;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Wintertodt;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;

public class MoveToBrazier extends Task {
  @Inject public Wintertodt plugin;

  @Override
  public String getStatus() {
    return "Moving near brazier";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInWintertodtRegion()) {
      return false;
    }

    if (plugin.isTooCold()
        || (Players.getLocal().getWorldLocation().equals(new WorldPoint(1634, 3987, 0))
            && plugin.wasPreviousActivity(Wintertodt.FLETCHING))) {
      return true;
    }

    return plugin.getRespawnTimer() > 0
        && !Players.getLocal()
            .getWorldLocation()
            .equals(plugin.getNearestSide().getPositionNearBrazier())
        && !Players.getLocal().isMoving();
  }

  @Override
  public void execute() {
    plugin.setTooCold(false);
    SluweMovement.walkTo(plugin.getNearestSide().getPositionNearBrazier());
    Time.sleepTick();
  }
}
