package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class ThrowPowder extends Task {
  @Override
  public String getStatus() {
    return "Throwing powder";
  }

  @Override
  public boolean validate() {
    return Players.getLocal().getWorldLocation().equals(Obstacle.POWDER_DISTRACT.getStart())
        && Inventory.contains(ItemID.FLASH_POWDER);
  }

  @Override
  public void execute() {
    final NPC guard = NPCs.getNearest(NpcID.ROGUE_GUARD_3191);
    if (guard == null) {
      return;
    }

    Inventory.getFirst(ItemID.FLASH_POWDER).useOn(guard);
    Time.sleepTicksUntil(() -> guard.getModelHeight() == 1000, 10);

    final WorldPoint destination = Obstacle.PENDULUM_FOUR.getStart();
    Movement.walk(destination);
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(destination), 20);
  }
}
