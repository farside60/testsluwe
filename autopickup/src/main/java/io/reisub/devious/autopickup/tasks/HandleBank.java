package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import javax.inject.Inject;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  @Inject private AutoPickup plugin;

  @Override
  public boolean validate() {
    return Inventory.isFull() && isLastBankDurationAgo(Duration.ofSeconds(5));
  }

  @Override
  public void execute() {
    if (plugin.getBankLocation() != null
        && Players.getLocal().distanceTo(plugin.getBankLocation()) > 8) {
      SluweMovement.walkTo(plugin.getBankLocation());
    }

    open();
    Bank.depositInventory();
  }
}
