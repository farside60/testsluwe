package io.reisub.devious.tithefarm.tasks;

import com.google.common.collect.ImmutableSet;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import java.util.Collection;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class FillCans extends Task {
  @Inject private TitheFarm plugin;
  private final Collection<Integer> nonFullWateringCanIds =
      ImmutableSet.of(
          ItemID.WATERING_CAN,
          ItemID.WATERING_CAN1,
          ItemID.WATERING_CAN2,
          ItemID.WATERING_CAN3,
          ItemID.WATERING_CAN4,
          ItemID.WATERING_CAN5,
          ItemID.WATERING_CAN6,
          ItemID.WATERING_CAN7);

  @Override
  public String getStatus() {
    return "Filling watering cans";
  }

  @Override
  public boolean validate() {
    return !plugin.isStartedRun() && Inventory.contains(Predicates.ids(nonFullWateringCanIds));
  }

  @Override
  public void execute() {
    final Item wateringCan = Inventory.getFirst(Predicates.ids(nonFullWateringCanIds));
    final TileObject barrel = TileObjects.getNearest(ObjectID.WATER_BARREL);

    if (wateringCan == null || barrel == null) {
      return;
    }

    wateringCan.useOn(barrel);
    Time.sleepTicksUntil(() -> !Inventory.contains(Predicates.ids(nonFullWateringCanIds)), 100);
  }
}
