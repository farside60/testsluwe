package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Inventory;

public class Eat extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Eating";
  }

  @Override
  public boolean validate() {
    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    if (Players.getLocal().getModelHeight() == 1000
        && Combat.getMissingHealth() >= config.eatThreshold()) {
      return true;
    }

    // failsafe, we should really eat at this health because of the potential damage we take
    // from failed blackjacks
    return Combat.getCurrentHealth() <= 12;
  }

  @Override
  public void execute() {
    Inventory.getFirst(ItemID.JUG_OF_WINE).interact("Drink");
    Time.sleepTick();
  }
}
