package io.reisub.devious.stallstealer.tasks;

import io.reisub.devious.stallstealer.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class Steal extends Task {
  @Inject private Config config;

  private TileObject stall;

  @Override
  public String getStatus() {
    return "Stealing";
  }

  @Override
  public boolean validate() {
    stall = TileObjects.getNearest(o -> o.getId() == config.stall().getStallId()
        && Players.getLocal().distanceTo(o.getWorldLocation()) < 3);

    return !Inventory.isFull()
        && stall != null
        && Players.getLocal().getWorldLocation().equals(config.stall().getStealLocation());
  }

  @Override
  public void execute() {
    int inventoryCount = Inventory.getAll().size();
    stall.interact("Steal-from");

    Time.sleepTicksUntil(() -> Inventory.getAll().size() > inventoryCount, 5);
  }
}
