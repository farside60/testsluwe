package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ObjectID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.widgets.Dialog;

public class CrackSafe extends Task {
  @Override
  public String getStatus() {
    return "Cracking safe";
  }

  @Override
  public boolean validate() {
    return Players.getLocal().getWorldLocation().equals(Obstacle.CRACK_SAFE.getStart());
  }

  @Override
  public void execute() {
    final WorldPoint safeLocation = new WorldPoint(3018, 5047, 1);
    Movement.walk(safeLocation);

    Time.sleepTicksUntil(
        () -> TileObjects.getFirstAt(safeLocation, ObjectID.WALL_SAFE_7237) != null, 10);

    TileObjects.getFirstAt(safeLocation, ObjectID.WALL_SAFE_7237).interact("Crack");
    Time.sleepTicksUntil(() -> Utils.isInRegion(12109) && Dialog.isOpen(), 20);
    Time.sleepTick();
  }
}
