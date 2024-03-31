package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class DouseFire extends Task {
  @Inject private Tempoross plugin;

  private NPC fire;

  @Override
  public String getStatus() {
    return "Dousing fire";
  }

  @Override
  public boolean validate() {
    fire = null;

    if (!plugin.isInTemporossArea()) {
      return false;
    }

    // don't douse flames when we're still getting the first 17 fish cooked
    if (plugin.getPhase() == 1
        && plugin.getRawFish() + plugin.getCookedFish() >= 17
        && plugin.getCookedFish() < 17
        && plugin.getCookedFish() > 10) {
      return false;
    }

    if (plugin.isCurrentActivity(Tempoross.STOCKING_CANNON)
        && plugin.getCookedFishRequired() <= 10) {
      return false;
    }

    // if we get fires beyond phase 1 there will be 1 fire on the boat that won't be picked up
    // by the normal check because it's out of range, but we need to douse it asap and the
    // easiest check is simply the player having exactly 2 buckets of water
    if (plugin.getPhase() != 1
        && Inventory.getCount(ItemID.BUCKET_OF_WATER) == 2
        && Players.getLocal().distanceTo(plugin.getDudiPos()) > 8) {
      return true;
    }

    fire =
        NPCs.getNearest(
            (n) ->
                n.getId() == NpcID.FIRE_8643
                    && (plugin.getIslandArea().contains(n) || plugin.getBoatArea().contains(n)));

    return Inventory.contains(ItemID.BUCKET_OF_WATER) && fire != null;
  }

  @Override
  public void execute() {
    // move to the boat to douse the fire on the boat in any phase other than phase 1
    if (fire == null && Players.getLocal().distanceTo(plugin.getDudiPos()) > 8) {
      Movement.walk(plugin.getDudiPos().dx(Rand.nextInt(-2, 3)).dy(Rand.nextInt(-2, 3)));
      Time.sleepTicksUntil(() -> Players.getLocal().distanceTo(plugin.getDudiPos()) <= 8, 20);

      fire =
          NPCs.getNearest(
              (n) ->
                  n.getId() == NpcID.FIRE_8643
                      && (plugin.getIslandArea().contains(n) || plugin.getBoatArea().contains(n)));
    }

    fire.interact(0);

    if (!Time.sleepUntil(() -> plugin.isCurrentActivity(Tempoross.DOUSING_FIRE), 2500)) {
      return;
    }

    Time.sleepUntil(() -> plugin.isCurrentActivity(Activity.IDLE), 15000);
  }
}
