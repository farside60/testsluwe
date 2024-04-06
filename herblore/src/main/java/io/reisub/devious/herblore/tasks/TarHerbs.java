package io.reisub.devious.herblore.tasks;

import io.reisub.devious.herblore.Herblore;
import io.reisub.devious.herblore.HerbloreTask;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Production;

public class TarHerbs extends Task {
  @Inject private Herblore plugin;

  @Override
  public String getStatus() {
    return "Tarring herbs";
  }

  @Override
  public boolean validate() {
    return plugin.getConfig().task() == HerbloreTask.TAR_HERBS
        && plugin.isCurrentActivity(Activity.IDLE)
        && Inventory.contains(ItemID.PESTLE_AND_MORTAR)
        && Inventory.getCount(true, ItemID.SWAMP_TAR) >= 15
        && Inventory.contains(plugin.getCleanTarHerbIds());
  }

  @Override
  public void execute() {
    plugin.setActivity(Herblore.TARRING_HERBS);

    List<Item> herbs = Inventory.getAll(plugin.getCleanTarHerbIds());
    Item swampTar = Inventory.getFirst(ItemID.SWAMP_TAR);

    swampTar.useOn(herbs.get(0));
    Time.sleepTicksUntil(Production::isOpen, 5);

    Production.chooseOption(1);
  }
}
