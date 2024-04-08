package io.reisub.sluwe.fletching.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.sluwe.fletching.Config;
import io.reisub.sluwe.fletching.Fletching;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Production;

public class Stringing extends Task {
  @Inject private Fletching plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Stringing";
  }

  @Override
  public boolean validate() {
    return plugin.isCurrentActivity(Activity.IDLE)
        && Inventory.contains(ItemID.BOW_STRING)
        && Inventory.contains(
            config.logType().getShortbowUnfinishedId(), config.logType().getLongbowUnfinishedId());
  }

  @Override
  public void execute() {
    if (Dialog.isOpen()) {
      Dialog.close();
    }

    final Item bowString = Inventory.getFirst(ItemID.BOW_STRING);
    final Item unfinishedBow =
        Inventory.getFirst(
            config.logType().getShortbowUnfinishedId(), config.logType().getLongbowUnfinishedId());

    bowString.useOn(unfinishedBow);
    Time.sleepTicksUntil(Production::isOpen, 3);

    Production.chooseOption(1);

    plugin.setActivity(Fletching.STRINGING);
  }
}
