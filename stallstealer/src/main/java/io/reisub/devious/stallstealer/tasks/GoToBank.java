package io.reisub.devious.stallstealer.tasks;

import io.reisub.devious.stallstealer.Config;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;

public class GoToBank extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to bank";
  }

  @Override
  public boolean validate() {
    return Inventory.isFull()
        && Players.getLocal().distanceTo(config.stall().getBankLocation()) >= 10;
  }

  @Override
  public void execute() {
    final WorldPoint destination = config.stall().getBankLocation();

    SluweMovement.walkTo(destination, 2);

    Time.sleepTicksUntil(() -> !Players.getLocal().isMoving()
        || Players.getLocal().distanceTo(destination) < 10, 15);
  }
}
