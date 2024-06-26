package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class FillBuckets extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Filling buckets";
  }

  @Override
  public boolean validate() {
    if (!Inventory.contains(ItemID.BUCKET)) {
      return false;
    }

    if (plugin.isCurrentActivity(Tempoross.FILLING_BUCKETS)) {
      return false;
    }

    if (plugin.isOnBoat()) {
      return true;
    }

    int filledBuckets = Inventory.getCount(ItemID.BUCKET_OF_WATER);
    int fires =
        NPCs.getAll(
                (n) ->
                    n.getId() == NpcID.FIRE_8643
                        && (plugin.getIslandArea().contains(n) || plugin.getBoatArea().contains(n)))
            .size();

    if ((plugin.isCurrentActivity(Activity.ATTACKING)
            || plugin.wasPreviousActivity(Activity.ATTACKING))
        && Players.getLocal().getInteracting() == null
        && NPCs.getNearest(
                (n) -> n.getId() == NpcID.SPIRIT_POOL && plugin.getIslandArea().contains(n))
            == null
        && Players.getLocal().distanceTo(plugin.getDudiPos()) > 8
        && plugin.getPhase() == 2
        && plugin.getCookedFishRequired() > 0
        && plugin.getCookedFishRequired() != 19) {
      return true;
    }

    return plugin.isInTemporossArea() && filledBuckets < fires;
  }

  @Override
  public void execute() {
    TileObject pump = TileObjects.getNearest(ObjectID.WATER_PUMP_41000, ObjectID.WATER_PUMP_41004);
    if (pump == null) {
      return;
    }

    pump.interact(0);

    if (!Time.sleepUntil(() -> plugin.isCurrentActivity(Tempoross.FILLING_BUCKETS), 15000)) {
      return;
    }

    Time.sleepUntil(() -> plugin.isCurrentActivity(Activity.IDLE), 5000);

    if (plugin.isOnBoat()) {
      Movement.walk(new WorldPoint(3137, 2840, 0));
    }
  }
}
