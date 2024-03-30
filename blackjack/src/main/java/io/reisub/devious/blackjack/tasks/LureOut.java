package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class LureOut extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;
  private List<NPC> npcsInRoom;

  @Override
  public String getStatus() {
    return "Luring target out of room";
  }

  @Override
  public boolean validate() {
    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    npcsInRoom = NPCs.getAll(n -> n.hasAction("Lure") && config.target().getRoom().contains(n));

    if (npcsInRoom.size() >= 2) {
      return true;
    } else if (npcsInRoom.size() == 1) {
      return npcsInRoom.get(0).getId() != config.target().getId();
    }

    return false;
  }

  @Override
  public void execute() {
    NPC target = npcsInRoom.get(0);

    // if we have multiple NPCs in the room, try to get one we don't want to pickpocket
    if (npcsInRoom.size() >= 2) {
      for (NPC npc : npcsInRoom) {
        if (npc.getId() != config.target().getId()) {
          target = npc;
        }
      }
    }

    final NPC finalTarget = target;

    if (finalTarget == null || !plugin.lure(finalTarget)) {
      return;
    }

    config
        .target()
        .getRoom()
        .passDoor(
            config.target().getRoom().getOutsideLocationTwo(),
            false,
            () ->
                finalTarget
                    .getWorldLocation()
                    .equals(config.target().getRoom().getOutsideLocation()),
            10);

    final NPC correctNpc =
        NPCs.getNearest(
            n -> n.getId() == config.target().getId() && config.target().getRoom().contains(n));

    // if the correct NPC is inside, go back inside and close the door
    // otherwise run away to leash the lure
    if (correctNpc != null) {
      config.target().getRoom().passDoor(config.target().getRoom().getInsideLocation());
    } else {
      Movement.walk(config.target().getRoom().getLeashLocation());
      Time.sleepTicksUntil(() -> finalTarget.getInteracting() == null, 20);

      final WorldPoint returnDestination = config.target().getRoom().getOutsideLocationTwo();
      Movement.walk(returnDestination);
      Time.sleepTicksUntil(() -> Players.getLocal().distanceTo(returnDestination) <= 5, 15);
    }
  }
}
