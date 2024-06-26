package io.reisub.devious.stallstealer.tasks;

import io.reisub.devious.stallstealer.Config;
import io.reisub.devious.utils.tasks.BankTask;
import javax.inject.Inject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  private final Config config;

  @Inject
  private HandleBank(Config config) {
    this.config = config;

    setWaitTicks(40);
  }

  @Override
  public boolean validate() {
    return Inventory.isFull()
        && Players.getLocal().distanceTo(config.stall().getBankLocation()) < 10;
  }

  @Override
  public void execute() {
    if (!open()) {
      return;
    }

    Bank.depositInventory();
    Time.sleepTicksUntil(() -> !Inventory.isFull(), 5);
  }
}
