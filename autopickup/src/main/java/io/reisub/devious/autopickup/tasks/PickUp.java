package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.autopickup.Config;
import io.reisub.devious.utils.api.SluwePredicates;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileItem;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;

public class PickUp extends Task {
  @Inject private AutoPickup plugin;
  @Inject private Config config;
  private TileItem nearestItem;

  @Override
  public String getStatus() {
    return "Picking up item";
  }

  @Override
  public boolean validate() {
    return (config.amount() == 0 || plugin.getItemsPickedUp() < plugin.getNumberOfItemsRequired())
        && !Inventory.isFull()
        && (nearestItem = plugin.getNearestItem()) != null;
  }

  @Override
  public void execute() {
    final int count =
        Inventory.getCount(true, SluwePredicates.itemConfigList(plugin.getItemsConfigList()));
    nearestItem.pickup();

    Time.sleepTicksUntil(
        () ->
            TileItems.getFirstAt(
                    nearestItem.getWorldLocation(),
                    SluwePredicates.entityConfigList(plugin.getItemsConfigList(), false, false))
                == null,
        20);

    final int itemsPickedUp =
        Inventory.getCount(true, SluwePredicates.itemConfigList(plugin.getItemsConfigList()))
            - count;
    plugin.setItemsPickedUp(plugin.getItemsPickedUp() + itemsPickedUp);
  }
}
