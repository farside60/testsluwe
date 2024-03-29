package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class OpenTileDoor extends Task {
  @Override
  public String getStatus() {
    return "Opening door with tile";
  }

  @Override
  public boolean validate() {
    return Inventory.contains(ItemID.TILE_5568);
  }

  @Override
  public void execute() {
    TileObject door = TileObjects.getNearest(ObjectID.DOOR_7234);
    if (door == null) {
      return;
    }

    door.interact("Open");
    Time.sleepTicksUntil(
        () -> {
          Widget widget = Widgets.fromId(45088773);
          return widget != null && widget.isVisible();
        },
        20);

    Widgets.fromId(45088773).interact("Select");
    Time.sleepTicksUntil(
        () -> Players.getLocal().getWorldLocation().equals(Obstacle.GRILL_THREE.getStart()), 10);
  }
}
