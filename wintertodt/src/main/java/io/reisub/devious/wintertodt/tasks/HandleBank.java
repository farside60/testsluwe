package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.tasks.BankTask;
import io.reisub.devious.utils.tasks.KittenTask;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Wintertodt;
import java.util.Arrays;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;

public class HandleBank extends BankTask {
  @Inject public Wintertodt plugin;
  @Inject public Config config;
  @Inject public io.reisub.devious.utils.Config utilsConfig;

  @Override
  public boolean validate() {
    return !plugin.isInWintertodtRegion()
        && isBankObjectAvailable()
        && (!config.openCrates()
            || !Inventory.contains(ItemID.SUPPLY_CRATE)
            || Inventory.getFreeSlots() < 5)
        && Inventory.getCount(i -> i.hasAction("Eat", "Drink")) <= 1;
  }

  @Override
  public void execute() {
    open();

    SluweBank.depositAllExcept(
        false,
        i -> {
          int[] ids =
              new int[] {
                ItemID.BRONZE_AXE,
                ItemID.IRON_AXE,
                ItemID.STEEL_AXE,
                ItemID.BLACK_AXE,
                ItemID.MITHRIL_AXE,
                ItemID.ADAMANT_AXE,
                ItemID.RUNE_AXE,
                ItemID.DRAGON_AXE,
                ItemID.HAMMER,
                ItemID.KNIFE,
                ItemID.TINDERBOX,
                ItemID._23_CAKE,
                ItemID.SLICE_OF_CAKE,
              };

          boolean match = Arrays.stream(ids).anyMatch(id -> i.getId() == id);
          if (match) {
            return true;
          }

          if (utilsConfig.handleKitten() && i.getName().equals(utilsConfig.kittenFood())) {
            return true;
          }

          if (config.openCrates() && i.getId() == ItemID.SUPPLY_CRATE) {
            return true;
          }

          return i.getName().equals(config.food());
        });

    if (config.openCrates() && Inventory.contains(ItemID.SUPPLY_CRATE)) {
      Bank.close();
      Time.sleepTick();
      return;
    }

    Bank.withdraw(config.food(), config.foodQuantity(), Bank.WithdrawMode.ITEM);

    KittenTask.withdrawKittenFood(utilsConfig);

    Time.sleepTicksUntil(() -> Inventory.getCount(i -> i.hasAction("Eat", "Drink")) > 1, 6);
  }
}
