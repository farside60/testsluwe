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
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Inventory;

public class Pickpocket extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;
  private NPC target;

  @Override
  public String getStatus() {
    return "Pickpocketing target";
  }

  @Override
  public boolean validate() {
    if (plugin.ticksSinceLastKnockout() >= 4) {
      return false;
    }

    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    target = NPCs.getNearest(config.target().getId());

    return target != null
        && config.target().getRoom().contains(Players.getLocal());
  }

  @Override
  public void execute() {
    if (target == null) {
      return;
    }

    GameThread.invoke(() -> target.interact("Pickpocket"));
    Time.sleepTicks(2);
  }
}
