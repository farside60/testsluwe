package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.autopickup.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileItem;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;

public class PickUpAsh extends Task {
  @Inject private AutoPickup plugin;
  @Inject private Config config;
  private TileItem nearestAsh;

  @Override
  public String getStatus() {
    return "Picking up ash";
  }

  @Override
  public boolean validate() {
    return config.pickUpAshes()
        && (config.ashAmount() == 0 || plugin.getAshesPickedUp() < config.ashAmount())
        && !Inventory.isFull()
        && (nearestAsh = plugin.getNearestAsh()) != null;
  }

  @Override
  public void execute() {
    final int count = Inventory.getCount(ItemID.ASHES);

    nearestAsh.pickup();

    Time.sleepTicksUntil(
        () -> TileItems.getFirstAt(nearestAsh.getWorldLocation(), ItemID.ASHES) == null, 20);

    final int ashesPickedUp = Inventory.getCount(ItemID.ASHES) - count;
    plugin.setAshesPickedUp(plugin.getAshesPickedUp() + ashesPickedUp);
  }
}
