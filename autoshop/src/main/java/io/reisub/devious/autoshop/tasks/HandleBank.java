package io.reisub.devious.autoshop.tasks;

import io.reisub.devious.autoshop.Config;
import io.reisub.devious.autoshop.Shopper;
import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class HandleBank extends BankTask {
  @Inject private Shopper plugin;

  @Inject private Config config;

  @Override
  public boolean validate() {
    return Inventory.isFull() && isLastBankDurationAgo(Duration.ofSeconds(3));
  }

  @Override
  public void execute() {
    if (config.disableRunFromShop() && Movement.isRunEnabled() && Movement.getRunEnergy() < 90) {
      Movement.toggleRun();
    }

    if (plugin.getBankLocation() != null) {
      SluweMovement.walkTo(plugin.getBankLocation(), 1);
    }

    if (!open()) {
      return;
    }

    SluweBank.depositAllExcept(false, "Coins", "Tokkul", "Coal bag");

    if (Inventory.contains(ItemID.COAL_BAG_12019) && plugin.getCoalInBag() > 0) {
      SluweBank.bankInventoryInteract(Bank.Inventory.getFirst(ItemID.COAL_BAG_12019), "Empty");
    }
  }
}
