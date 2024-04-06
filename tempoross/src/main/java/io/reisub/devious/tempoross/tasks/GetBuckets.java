package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.api.interaction.checks.InventoryContainsCheck;
import io.reisub.devious.utils.tasks.Task;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
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
    int amountToDrop = 5 - Inventory.getFreeSlots();

    if (amountToDrop > 0) {
      List<Item> fish = Inventory.getAll(ItemID.RAW_HARPOONFISH, ItemID.HARPOONFISH);
      fish.sort(Comparator.comparingInt(Item::getId));

      for (int i = 0; i < amountToDrop; i++) {
        fish.get(i).drop();
      }
    }

    final TileObject buckets = TileObjects.getNearest(ObjectID.BUCKETS);

    new Interaction(buckets, "Take-5", new InventoryContainsCheck(20, ItemID.BUCKET)).interact();
  }
}
