package io.reisub.devious.smelter.tasks;

import io.reisub.devious.smelter.Config;
import io.reisub.devious.smelter.Smelter;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.client.Static;

public class HandleBank extends BankTask {
  @Inject private Smelter plugin;
  @Inject private Config config;

  @Override
  public boolean validate() {
    return !config.product().hasMaterials() && isLastBankDurationAgo(Duration.ofSeconds(3));
  }

  @Override
  public void execute() {
    if (!config.location().getBankLocation().isInScene(Static.getClient())) {
      SluweMovement.walkTo(config.location().getBankLocation(), 1);
    }

    if (!Movement.isRunEnabled() && Movement.getRunEnergy() > 50) {
      Movement.toggleRun();
    }

    if (!open()) {
      return;
    }

    Bank.depositInventory();

    config.product().getMaterials().forEach((id, amount) -> {
      if (!Bank.contains(id) || Bank.getFirst(id).isPlaceholder()) {
        plugin.stop("No more materials, stopping plugin.");
      }

      Bank.withdraw(id, amount, Bank.WithdrawMode.ITEM);
    });

    last = Instant.now();
    plugin.setLastBankTick(Static.getClient().getTickCount());
  }
}
