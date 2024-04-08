package io.reisub.sluwe.fletching.tasks;

import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.sluwe.fletching.Fletching;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class PickupSeed extends Task {
  @Inject private Fletching plugin;

  @Override
  public String getStatus() {
    return "Picking up seed";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(Fletching.FOSSIL_ISLAND_SEAWEED_REGION)
        && TileItems.getNearest(ItemID.SEAWEED_SPORE) != null;
  }

  @Override
  public void execute() {
    plugin.setActivity(Activity.IDLE);

    final int count = Inventory.getCount(true, ItemID.SEAWEED_SPORE);
    final TileItem spore = TileItems.getNearest(ItemID.SEAWEED_SPORE);

    spore.interact("Take");
    Time.sleepTicksUntil(() -> Inventory.getCount(true, ItemID.SEAWEED_SPORE) > count, 30);

    if (TileItems.getNearest(ItemID.SEAWEED_SPORE) == null) {
      Movement.walk(new WorldPoint(3731, 10281, 1));
    }
  }
}
