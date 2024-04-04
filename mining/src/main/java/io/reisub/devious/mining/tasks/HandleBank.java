package io.reisub.devious.mining.tasks;

import io.reisub.devious.mining.Config;
import io.reisub.devious.mining.Location;
import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.tasks.BankTask;
import java.time.Duration;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.SpellBook;

public class HandleBank extends BankTask {
  private final Config config;

  @Inject
  private HandleBank(Config config) {
    this.config = config;
    setOpenMainTab(true);
  }

  @Override
  public boolean validate() {
    return Inventory.isFull()
        && config.location().getBankPoint() != null
        && !config.drop()
        && Players.getLocal().distanceTo(config.location().getBankPoint()) < 10
        && isLastBankDurationAgo(Duration.ofSeconds(5))
        && !shouldSuperheat();
  }

  @Override
  public void execute() {
    if (config.location() == Location.BASALT) {
      final Item basalt = Inventory.getFirst(ItemID.BASALT);
      final NPC snowflake = NPCs.getNearest(NpcID.SNOWFLAKE);

      if (basalt == null || snowflake == null) {
        return;
      }

      GameThread.invoke(() -> basalt.useOn(snowflake));

      Time.sleepTicksUntil(() -> !Inventory.contains(ItemID.BASALT), 20);
      return;
    }

    open();

    final Item gemBag = Bank.Inventory.getFirst(ItemID.OPEN_GEM_BAG);

    if (gemBag != null) {
      SluweBank.bankInventoryInteract(gemBag, "Empty");
      Time.sleepTick();
    }

    SluweBank.depositAllExcept(
        false,
        ItemID.OPEN_GEM_BAG,
        ItemID.ARDOUGNE_CLOAK_1,
        ItemID.ARDOUGNE_CLOAK_2,
        ItemID.ARDOUGNE_CLOAK_3,
        ItemID.ARDOUGNE_CLOAK_4,
        ItemID.RUNE_PICKAXE,
        ItemID.NATURE_RUNE);

    if (config.location() == Location.SOFT_CLAY
        && !Equipment.contains(ItemID.BRACELET_OF_CLAY)
        && Bank.contains(ItemID.BRACELET_OF_CLAY)) {
      Time.sleepTick();
      Bank.withdraw(ItemID.BRACELET_OF_CLAY, 1, WithdrawMode.ITEM);
    }
  }

  public boolean shouldSuperheat() {
    return config.superheat()
        && config.location() == Location.MONASTERY_IRON
        && SpellBook.Standard.SUPERHEAT_ITEM.canCast()
        && Inventory.contains(ItemID.IRON_ORE);
  }
}
