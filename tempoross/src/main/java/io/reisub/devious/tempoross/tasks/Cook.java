package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.client.Static;

public class Cook extends Task {
  @Inject private Tempoross plugin;

  @Override
  public String getStatus() {
    return "Cooking";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea()) {
      return false;
    }

    if (plugin.getRawFish() == 0) {
      return false;
    }

    if (plugin.isCurrentActivity(Activity.ATTACKING)) {
      return false;
    }

    if (plugin.getPhase() == 1
        && plugin.getEnergy() < 100
        && plugin.getRawFish() + plugin.getCookedFish() >= 19
        && plugin.isCurrentActivity(Tempoross.FISHING)
        && (93 - plugin.getStormIntensity())
            > plugin.getCookedFishRequired() - plugin.getCookedFish()) {
      return false;
    }

    if (plugin.getPhase() == 1
        && plugin.getEnergy() < 100
        && plugin.getRawFish() + plugin.getCookedFish() >= 19
        && (plugin.isCurrentActivity(Tempoross.FISHING)
            || plugin.isCurrentActivity(Activity.IDLE))) {
      return true;
    }

    if (plugin.getPhase() == 2
        && plugin.getCookedFishRequired() > 0
        && plugin.getCookedFishRequired() != 19
        && plugin.getRawFish() + plugin.getCookedFish() >= plugin.getCookedFishRequired()
        && (plugin.isCurrentActivity(Tempoross.FISHING)
            || plugin.isCurrentActivity(Activity.IDLE))) {
      return true;
    }

    NPC doubleSpot =
        NPCs.getNearest(
            (n) -> n.getId() == NpcID.FISHING_SPOT_10569 && plugin.getIslandArea().contains(n));

    if (doubleSpot != null
        && !Inventory.isFull()
        && !(plugin.getPhase() == 1
            && plugin.getCookedFishRequired() == 19
            && plugin.getRawFish() + plugin.getCookedFish() >= 19)) {
      return false;
    }

    NPC fishingSpot = NPCs.getNearest(NpcID.FISHING_SPOT_10569, NpcID.FISHING_SPOT_10565);

    if (plugin.wasPreviousActivity(Tempoross.FISHING)
        && Inventory.getFreeSlots() <= 2
        && !Inventory.isFull()
        && fishingSpot != null
        && Players.getLocal().distanceTo(fishingSpot) <= 8) {
      return false;
    }

    if (plugin.isCurrentActivity(Tempoross.FISHING)
        && Inventory.getCount(ItemID.RAW_HARPOONFISH) >= 9
        && doubleSpot == null) {
      return true;
    }

    return plugin.isCurrentActivity(Activity.IDLE);
  }

  @Override
  public void execute() {
    WorldPoint target = plugin.getDudiPos().dx(7).dy(16);

    if (Players.getLocal().getWorldLocation().getY() < target.getY() - 5) {
      Movement.walk(target.dx(Rand.nextInt(-2, 3)).dy(Rand.nextInt(-2, 3)));

      if (!Time.sleepUntil(() -> Players.getLocal().isMoving(), 1500)) {
        return;
      }

      Time.sleepUntil(
          () ->
              Players.getLocal().getWorldLocation().getY() >= target.getY() - Rand.nextInt(4, 6)
                  || plugin.isWaveIncoming(),
          10000);
    }

    NPC doubleSpot =
        NPCs.getNearest(
            (n) -> n.getId() == NpcID.FISHING_SPOT_10569 && plugin.getIslandArea().contains(n));

    if (doubleSpot != null
        && !Inventory.isFull()
        && !(plugin.getPhase() == 1
            && plugin.getCookedFishRequired() == 19
            && plugin.getRawFish() + plugin.getCookedFish() >= 19)) {
      return;
    }

    if (plugin.isWaveIncoming()) {
      return;
    }

    TileObject shrine = TileObjects.getNearest(ObjectID.SHRINE_41236);
    if (shrine == null) {
      return;
    }

    shrine.interact(0);
    Time.sleepUntil(
        () ->
            plugin.isCurrentActivity(Tempoross.COOKING)
                || plugin.isWaveIncoming()
                || plugin.getLastDoubleSpawn() + 3 >= Static.getClient().getTickCount(),
        10000);
  }
}
