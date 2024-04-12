package io.reisub.devious.motherlodemine.tasks;

import io.reisub.devious.motherlodemine.MotherlodeMine;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.api.SluweInventory;
import io.reisub.devious.utils.tasks.BankTask;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class HandleBank extends BankTask {
  @Inject private MotherlodeMine plugin;

  private int lastGemBagEmpty;

  @Override
  public boolean validate() {
    return plugin.isCurrentActivity(Activity.IDLE)
        && !plugin.isUpstairs()
        && Inventory.contains(
            ItemID.RUNITE_ORE,
            ItemID.ADAMANTITE_ORE,
            ItemID.MITHRIL_ORE,
            ItemID.GOLD_ORE,
            ItemID.COAL,
            ItemID.UNCUT_SAPPHIRE,
            ItemID.UNCUT_EMERALD,
            ItemID.UNCUT_RUBY,
            ItemID.UNCUT_DIAMOND,
            ItemID.UNCUT_DRAGONSTONE);
  }

  @Override
  public void execute() {
    open();

    final Item gemBag = Bank.Inventory.getFirst(ItemID.OPEN_GEM_BAG);

    if (gemBag != null && Static.getClient().getTickCount() - lastGemBagEmpty > 100) {
      gemBag.interact("Empty");
      lastGemBagEmpty = Static.getClient().getTickCount();
      Time.sleepTick();
    }

    SluweBank.depositAllExcept(
        false, ItemID.IMCANDO_HAMMER, ItemID.HAMMER, ItemID.GOLDEN_NUGGET, ItemID.OPEN_GEM_BAG);

    Time.sleepTicksUntil(
        () ->
            !Inventory.contains(
                ItemID.RUNITE_ORE,
                ItemID.ADAMANTITE_ORE,
                ItemID.MITHRIL_ORE,
                ItemID.GOLD_ORE,
                ItemID.COAL,
                ItemID.UNCUT_SAPPHIRE,
                ItemID.UNCUT_EMERALD,
                ItemID.UNCUT_RUBY,
                ItemID.UNCUT_DIAMOND,
                ItemID.UNCUT_DRAGONSTONE),
        3);

    if (!SluweInventory.hasHammer()) {
      Bank.withdraw(
          Predicates.ids(ItemID.IMCANDO_HAMMER, ItemID.HAMMER), 1, Bank.WithdrawMode.ITEM);
      Time.sleepTick();
    }
  }
}
