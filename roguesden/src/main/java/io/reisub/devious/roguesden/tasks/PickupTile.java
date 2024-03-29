package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ItemID;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;

public class PickupTile extends Task {
  @Override
  public String getStatus() {
    return "Picking up tile";
  }

  @Override
  public boolean validate() {
    return Players.getLocal().getWorldLocation().equals(Obstacle.TILE.getStart());
  }

  @Override
  public void execute() {
    TileItem tile = TileItems.getFirstAt(new WorldPoint(3018, 5080, 1), "Tile");
    if (tile == null) {
      return;
    }

    tile.pickup();
    Time.sleepTicksUntil(() -> Inventory.contains(ItemID.TILE_5568), 20);
  }
}
