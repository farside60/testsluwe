package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ItemID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class PickupPowder extends Task {
  @Override
  public String getStatus() {
    return "Picking up sand";
  }

  @Override
  public boolean validate() {
    return Players.getLocal().getWorldLocation().equals(Obstacle.POWDER.getStart())
        || (Players.getLocal().getWorldLocation().equals(Obstacle.POWDER_DISTRACT.getStart())
            && !Inventory.contains(ItemID.FLASH_POWDER));
  }

  @Override
  public void execute() {
    final WorldPoint powderTile = Obstacle.POWDER_DISTRACT.getStart();
    if (Players.getLocal().getWorldLocation().equals(powderTile)) {
      Movement.walk(powderTile);
      Time.sleepTicksUntil(() -> TileItems.getFirstAt(powderTile, ItemID.FLASH_POWDER) != null, 20);
    }

    TileItems.getFirstAt(powderTile, ItemID.FLASH_POWDER).pickup();
    Time.sleepTicksUntil(() -> Inventory.contains(ItemID.FLASH_POWDER), 10);
  }
}
