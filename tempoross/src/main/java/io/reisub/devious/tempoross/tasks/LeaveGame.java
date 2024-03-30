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
import net.unethicalite.api.widgets.Dialog;

public class LeaveGame extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Leaving game";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea()) {
      return false;
    }

    NPC pudi = NPCs.getNearest(NpcID.CAPTAIN_PUDI_10585, NpcID.CAPTAIN_PUDI_10586);

    if (pudi != null && Players.getLocal().distanceTo(pudi) < 5) {
      return true;
    }

    if (Players.getAll().size() > 1) {
      return true;
    }

    NPC fire =
        NPCs.getNearest(
            (n) ->
                n.getId() == NpcID.FIRE_8643
                    && (plugin.getIslandArea().contains(n) || plugin.getBoatArea().contains(n)));

    if (fire != null
        && !Inventory.contains(ItemID.BUCKET)
        && !Inventory.contains(ItemID.BUCKET_OF_WATER)) {
      return true;
    }

    return plugin.isFinished();
  }

  @Override
  public void execute() {
    if (!Inventory.contains(ItemID.BUCKET) && !Inventory.contains(ItemID.BUCKET_OF_WATER)) {
      TileObject buckets = TileObjects.getNearest(ObjectID.BUCKETS);
      if (buckets == null) {
        return;
      }

      buckets.interact("Take-5");
      Time.sleepUntil(() -> Inventory.contains(ItemID.BUCKET), 100, 10000);
    }

    NPC leaveNpc = NPCs.getNearest(n -> n.hasAction("Forfeit", "Leave"));
    if (leaveNpc == null) {
      return;
    }

    leaveNpc.interact("Leave", "Forfeit");
    Time.sleepUntil(() -> plugin.isInDesert(), 20000);

    Time.sleepUntil(Dialog::canContinueNPC, 100, 30000);
  }
}
