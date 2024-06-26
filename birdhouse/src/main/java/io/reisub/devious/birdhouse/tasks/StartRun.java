package io.reisub.devious.birdhouse.tasks;

import io.reisub.devious.birdhouse.BirdHouse;
import io.reisub.devious.birdhouse.Config;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.SluweBank;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.enums.HouseTeleport;
import io.reisub.devious.utils.enums.TeleportLocation;
import io.reisub.devious.utils.tasks.BankTask;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Bank.WithdrawMode;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;

public class StartRun extends BankTask {
  private final BirdHouse plugin;
  private final Config config;

  @Inject
  private StartRun(BirdHouse plugin, Config config) {
    this.plugin = plugin;
    this.config = config;

    setOpenMainTab(true);
  }

  @Override
  public String getStatus() {
    return "Starting bird house run";
  }

  @Override
  public boolean validate() {
    return plugin.isManuallyStarted();
  }

  @Override
  public void execute() {
    open();

    if (!Inventory.isEmpty()) {
      final Item gemBag = Bank.Inventory.getFirst(Predicates.nameContains("gem bag", false));

      if (gemBag != null) {
        gemBag.interact("Empty");
      }

      Bank.depositInventory();

      if (config.equipGraceful() && SluweBank.haveGracefulInBank()) {
        Bank.depositEquipment();
      }

      Time.sleepTick();
    }

    Bank.withdraw(
        i -> i.getId() == ItemID.IMCANDO_HAMMER || i.getId() == ItemID.HAMMER,
        1,
        Bank.WithdrawMode.ITEM);
    Bank.withdraw(ItemID.CHISEL, 1, Bank.WithdrawMode.ITEM);
    Bank.withdraw(config.logs().getId(), 4, Bank.WithdrawMode.ITEM);
    Bank.withdraw(getSeedId(), 40, Bank.WithdrawMode.ITEM);

    if (config.goThroughHouse()
        || (!Bank.contains(Predicates.ids(Constants.DIGSITE_PENDANT_IDS))
            && config.goThroughHouseFallback())) {
      TeleportLocation.HOUSE_TELEPORT.withdrawItems(true, true, config.useHouseTab());
    } else {
      Bank.withdraw(Predicates.ids(Constants.DIGSITE_PENDANT_IDS), 1, Bank.WithdrawMode.ITEM);
    }

    Time.sleepTick();

    config
        .tpLocation()
        .withdrawItems(
            config.goThroughHouse(), config.goThroughHouseFallback(), config.useHouseTab());

    if (config.equipGraceful() && SluweBank.haveGracefulInBank()) {
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_CAPE), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_BOOTS), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_GLOVES), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_HOOD), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_TOP), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.GRACEFUL_LEGS), 1, Bank.WithdrawMode.ITEM);
      Bank.withdraw(Predicates.ids(Constants.RING_OF_ENDURANCE_IDS), 1, WithdrawMode.ITEM);
    }

    close();
    Time.sleepTicksUntil(() -> !Bank.isOpen(), 5);

    Inventory.getAll(i -> i.getName().startsWith("Graceful")).forEach(i -> i.interact("Wear"));
    final Item roe = Inventory.getFirst(Predicates.ids(Constants.RING_OF_ENDURANCE_IDS));
    if (roe != null) {
      roe.interact("Wear");
    }

    if (!Time.sleepTicksUntil(this::hasEverything, 3)) {
      return;
    }

    if (Dialog.isOpen()) {
      Dialog.close();
    }

    final Item pendant = Inventory.getFirst(Predicates.ids(Constants.DIGSITE_PENDANT_IDS));

    if (pendant == null) {
      SluweMovement.teleportThroughHouse(HouseTeleport.FOSSIL_ISLAND);
    } else {
      pendant.interact("Rub");
      Time.sleepTicksUntil(Dialog::isViewingOptions, 5);

      Dialog.chooseOption(2);
    }

    plugin.setManuallyStarted(false);
  }

  public int getSeedId() {
    for (int id : Constants.BIRD_HOUSE_SEED_IDS) {
      if (Bank.getCount(true, id) >= 40) {
        return id;
      }
    }

    return 0;
  }

  private boolean hasEverything() {
    return (Inventory.contains(ItemID.IMCANDO_HAMMER, ItemID.HAMMER)
            || Equipment.contains(ItemID.IMCANDO_HAMMER))
        && Inventory.contains(ItemID.CHISEL)
        && (Inventory.contains(Predicates.ids(Constants.DIGSITE_PENDANT_IDS))
            || (Inventory.contains(ItemID.AIR_RUNE)
                && Inventory.contains(ItemID.EARTH_RUNE)
                && Inventory.contains(ItemID.LAW_RUNE))
            || Inventory.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS)))
        && Inventory.getCount(config.logs().getId()) == 4
        && Inventory.getCount(true, Predicates.ids(Constants.BIRD_HOUSE_SEED_IDS)) == 40;
  }
}
