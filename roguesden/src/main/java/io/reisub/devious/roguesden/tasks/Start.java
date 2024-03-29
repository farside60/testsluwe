package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class Start extends Task {
  @Override
  public String getStatus() {
    return "Starting course";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(12109)
        && Players.getLocal().getWorldLocation().getY() < 4992
        && Inventory.getFreeSlots() == 28
        && Equipment.getAll().isEmpty();
  }

  @Override
  public void execute() {
    TileObject doorway = TileObjects.getNearest(ObjectID.DOORWAY_7256);
    if (doorway == null) {
      final WorldPoint destination = new WorldPoint(3055, 4984, 1);
      SluweMovement.walk(destination, 2);
      Time.sleepTicksUntil(() -> TileObjects.getNearest(ObjectID.DOORWAY_7256) != null, 10);
      doorway = TileObjects.getNearest(ObjectID.DOORWAY_7256);
    }

    doorway.interact("Open");
    Time.sleepTicksUntil(
        () -> Players.getLocal().getWorldLocation().equals(Obstacle.CONTORTION_BARS.getStart()),
        30);
    Time.sleepTick();
  }
}
