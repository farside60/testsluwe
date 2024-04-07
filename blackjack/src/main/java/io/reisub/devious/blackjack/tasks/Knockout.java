package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;

public class Knockout extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;
  private NPC target;

  @Override
  public String getStatus() {
    return "Knock-out target";
  }

  @Override
  public boolean validate() {
    if (plugin.ticksSinceLastKnockout() < 4) {
      return false;
    }

    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    if (Players.getLocal().getModelHeight() == 1000) {
      return false;
    }

    target =
        NPCs.getNearest(
            n ->
                n.getId() == config.target().getId()
                    && Reachable.isInteractable(n)
                    && config.target().getRoom().contains(n));

    return target != null && config.target().getRoom().contains(Players.getLocal());
  }

  @Override
  public void execute() {
    plugin.setOriginalWorld(Worlds.getCurrentId());

    if (target == null) {
      return;
    }

    target.interact("Knock-Out");

    if (Players.getLocal().distanceTo(target) > 1) {
      Time.sleepTicksUntil(() -> !Players.getLocal().isMoving(), 5);
    }

    Time.sleepTicks(2);
  }
}
