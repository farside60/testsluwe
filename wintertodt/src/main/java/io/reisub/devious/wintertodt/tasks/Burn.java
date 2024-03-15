package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Side;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class Burn extends Task {
  @Inject public Wintertodt plugin;

  private TileObject brazier;

  @Override
  public String getStatus() {
    return "Feeding brazier";
  }

  @Override
  public boolean validate() {
    if (!plugin.bossIsUp()) {
      return false;
    }

    if (plugin.wasPreviousActivity(Wintertodt.WOODCUTTING)
        && !Inventory.isFull()
        && !plugin.shouldStartFeeding()) {
      return false;
    }

    brazier =
        TileObjects.getFirstSurrounding(
            Players.getLocal().getWorldLocation(), 8, "Burning brazier");

    return plugin.isCurrentActivity(Activity.IDLE)
        && Inventory.contains(ItemID.BRUMA_ROOT, ItemID.BRUMA_KINDLING)
        && brazier != null;
  }

  @Override
  public void execute() {
    if (plugin.getNearestSide() == Side.WEST
        && !Players.getLocal().getWorldLocation().equals(Side.WEST.getPositionNearBrazier())) {
      Movement.walk(Side.WEST.getPositionNearBrazier());
      Time.sleepTick();
      Time.sleepTicksUntil(
          () ->
              !Players.getLocal().isMoving()
                  || Players.getLocal()
                      .getWorldLocation()
                      .equals(Side.WEST.getPositionNearBrazier()),
          20);
    }

    brazier.interact("Feed");

    Time.sleepTicksUntil(
        () ->
            plugin.isCurrentActivity(Wintertodt.FEEDING_BRAZIER)
                || TileObjects.getFirstSurrounding(
                        Players.getLocal().getWorldLocation(), 15, "Burning brazier")
                    == null,
        10);

    Time.sleepTick();
  }
}
