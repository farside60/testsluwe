package io.reisub.devious.woodcutting.tasks;

import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class MoveToBurnLine extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  private WorldPoint nearestStart;

  @Override
  public String getStatus() {
    return "Moving to start of burn line";
  }

  @Override
  public Activity getActivity() {
    return Woodcutting.BURNING;
  }

  @Override
  public boolean validate() {
    if (!config.burn() || config.location().getBurnLineStarts() == null) {
      return false;
    }

    if (plugin.isCurrentActivity(Woodcutting.BURNING)) {
      return false;
    }

    if (!Inventory.contains(Predicates.ids(Constants.LOG_IDS))) {
      return false;
    }

    if (plugin.wasPreviousActivity(Woodcutting.BURNING)
        && plugin.isCurrentActivity(Activity.IDLE)) {
      nearestStart = findNearestFreeStartPoint();

      return nearestStart != null;
    }

    if (!Inventory.isFull()
        && !TileObjects.getAll(Predicates.ids(config.location().getTreeIds())).isEmpty()) {
      return false;
    }

    // Most expensive check so we're doing this last
    nearestStart = findNearestFreeStartPoint();

    return nearestStart != null;
  }

  @Override
  public void execute() {
    if (!nearestStart.equals(Players.getLocal().getWorldLocation())) {
      Movement.walk(nearestStart);
      Time.sleepTicks(2);
    }

    Time.sleepTicksUntil(
        () ->
            !Players.getLocal().isMoving()
                || Players.getLocal().getWorldLocation().equals(nearestStart),
        20);
  }

  private WorldPoint findNearestFreeStartPoint() {
    WorldPoint nearest = null;

    for (WorldPoint startPoint : config.location().getBurnLineStarts()) {
      if (!TileObjects.getAt(startPoint, "Fire").isEmpty()) {
        continue;
      }

      if (nearest == null
          || Players.getLocal().distanceTo(startPoint) < Players.getLocal().distanceTo(nearest)) {
        nearest = startPoint;
      }
    }

    return nearest;
  }
}
