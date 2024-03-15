package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class Chop extends Task {
  @Inject public Wintertodt plugin;

  @Override
  public String getStatus() {
    return "Chopping bruma roots";
  }

  @Override
  public boolean validate() {
    if (!plugin.bossIsUp()) {
      return false;
    }

    return (plugin.isCurrentActivity(Activity.IDLE) || plugin.isCurrentActivity(Activity.EATING))
        && !plugin.shouldStartFeeding()
        && !plugin.shouldStartFletching()
        && plugin.getBossHealth() > 4
        && !Inventory.isFull();
  }

  @Override
  public void execute() {
    WorldPoint nearRoots = plugin.getNearestSide().getPositionNearRoots();

    if (!Players.getLocal().getWorldLocation().equals(nearRoots)) {
      Movement.walk(nearRoots);
      Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), 3);
      Time.sleepTicksUntil(
          () ->
              !Players.getLocal().isMoving()
                  || Players.getLocal().getWorldLocation().equals(nearRoots),
          20);
    }

    final TileObject root = TileObjects.getNearest(ObjectID.BRUMA_ROOTS);
    if (root == null) {
      return;
    }

    root.interact("Chop");
    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(Wintertodt.WOODCUTTING), 20);
    Time.sleepTick();
  }
}
