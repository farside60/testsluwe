package io.reisub.devious.cooking.tasks;

import io.reisub.devious.cooking.Config;
import io.reisub.devious.cooking.Cooking;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import lombok.RequiredArgsConstructor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

@RequiredArgsConstructor
public class HandleBank extends BankTask {
  private static final int ROGUES_DEN_REGION = 12109;

  private final Cooking plugin;
  private final Config config;

  @Override
  public boolean validate() {
    return !Inventory.contains(config.food())
        && (!config.sonicMode()
            || TileItems.getFirstAt(Players.getLocal().getWorldLocation(), config.food()) == null)
        && isLastBankDurationAgo(Duration.ofSeconds(5));
  }

  @Override
  public void execute() {
    if (Players.getLocal().getWorldLocation().getRegionID() == ROGUES_DEN_REGION) {
      setName("Emerald Benedict");
      open();
    } else {
      open();
    }

    Bank.depositInventory();

    if (!Bank.contains(config.food()) || Bank.getFirst(config.food()).isPlaceholder()) {
      plugin.stop("No more raw food to cook, stopping plugin");
    }

    if (config.food().equals("Giant seaweed")) {
      Bank.withdraw(config.food(), 4, Bank.WithdrawMode.ITEM);
    } else {
      Bank.withdrawAll(config.food(), Bank.WithdrawMode.ITEM);
    }

    plugin.setLastBank(Static.getClient().getTickCount());

    close();
  }
}
