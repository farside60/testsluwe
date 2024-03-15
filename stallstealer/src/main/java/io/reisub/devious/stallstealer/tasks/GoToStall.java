package io.reisub.devious.stallstealer.tasks;

import io.reisub.devious.stallstealer.Config;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class GoToStall extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to stall";
  }

  @Override
  public boolean validate() {
    return !Inventory.isFull()
        && !Players.getLocal().getWorldLocation().equals(config.stall().getStealLocation());
  }

  @Override
  public void execute() {
    final WorldPoint destination = config.stall().getStealLocation();

    if (Players.getLocal().distanceTo(destination) > 8) {
      SluweMovement.walkTo(config.stall().getStealLocation());
      Time.sleepTick();
    }

    Movement.walk(destination);

    Time.sleepTicksUntil(() -> !Players.getLocal().isMoving()
        || Players.getLocal().getWorldLocation().equals(destination), 15);
  }
}
