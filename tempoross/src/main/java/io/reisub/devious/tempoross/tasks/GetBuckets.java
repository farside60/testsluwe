package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class GetBuckets extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Getting buckets";
  }

  @Override
  public boolean validate() {
    if (Inventory.contains(ItemID.BUCKET) || Inventory.contains(ItemID.BUCKET_OF_WATER)) {
      return false;
    }

    if (plugin.isFinished()) {
      return true;
    }

    if (plugin.isInTemporossArea() && Players.getAll().size() > 1) {
      return true;
    }

    NPC fire =
        NPCs.getNearest(
            (n) ->
                n.getId() == NpcID.FIRE_8643
                    && (plugin.getIslandArea().contains(n) || plugin.getBoatArea().contains(n)));

    return fire != null;
  }

  @Override
  public void execute() {
    TileObject buckets = TileObjects.getNearest(ObjectID.BUCKETS);
    if (buckets == null) {
      return;
    }

    buckets.interact("Take-5");
    Time.sleepTicksUntil(() -> Inventory.contains(ItemID.BUCKET), 20);
  }
}
