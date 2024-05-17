package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.autopickup.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Production;

public class Fletch extends Task {
  @Inject private AutoPickup plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Fletching arrow shafts";
  }

  @Override
  public boolean validate() {
    if (!config.fletchArrowShafts()) {
      return false;
    }

    if (!Inventory.contains(ItemID.KNIFE)) {
      return false;
    }

    if (!Inventory.contains(ItemID.LOGS)) {
      return false;
    }

    if (config.arrowShaftAmount() != 0
        && plugin.getArrowShaftCount() >= config.arrowShaftAmount()) {
      return false;
    }

    final int arrowShaftsWeCanMake = Inventory.getCount(ItemID.LOGS) * 15;

    return Inventory.isFull()
        || plugin.getArrowShaftCount() + arrowShaftsWeCanMake >= config.arrowShaftAmount();
  }

  @Override
  public void execute() {
    final Item knife = Inventory.getFirst(ItemID.KNIFE);
    final Item logs = Inventory.getFirst(ItemID.LOGS);
    if (knife == null || logs == null) {
      return;
    }

    final int startCount = Inventory.getCount(true, ItemID.ARROW_SHAFT);

    knife.useOn(logs);
    Time.sleepTicksUntil(Production::isOpen, 5);

    Production.chooseOption(1);
    Time.sleepTicksUntil(() -> !Dialog.isOpen(), 3);
    Time.sleepTicksUntil(() -> !Inventory.contains(ItemID.LOGS) || Dialog.isOpen(), 100);

    final int arrowShaftsMade = Inventory.getCount(true, ItemID.ARROW_SHAFT) - startCount;

    plugin.setArrowShaftCount(plugin.getArrowShaftCount() + arrowShaftsMade);
  }
}
