package io.reisub.devious.woodcutting.tasks.forestry;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
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

public class Pheasants extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Stealing egg";
  }

  @Override
  public boolean validate() {
    return config.forestryPheasants()
        && NPCs.getNearest(NpcID.FREAKY_FORESTER_12536) != null;
  }

  @Override
  public void execute() {
    plugin.setActivity(Woodcutting.FORESTRY);

    // if we're stunned, wait until we're not anymore
    // we check this here instead of in validate so that we don't try doing any other tasks when
    // stunned
    if (Players.getLocal().getModelHeight() == 1000) {
      Time.sleepTicksUntil(() -> Players.getLocal().getModelHeight() != 1000, 10);
      Time.sleepTick();
    }

    TileObject nest = TileObjects.getNearest(ObjectID.PHEASANT_NEST_49937);
    if (nest == null) {
      return;
    }

    if (Inventory.isFull()) {
      Inventory.getFirst(i -> i.getName().contains("logs")).drop();
      Time.sleepTick();
    }

    final WorldPoint nestLocation = nest.getWorldLocation();

    nest.interact(0);
    Time.sleepTicksUntil(
        () ->
            Inventory.contains(ItemID.PHEASANT_EGG)
                || TileObjects.getFirstAt(nestLocation, ObjectID.PHEASANT_NEST_49937) == null,
        10);

    // interrupt if pheasant nest is not safe anymore
    if (!Inventory.contains(ItemID.PHEASANT_EGG)
        && TileObjects.getFirstAt(nestLocation, ObjectID.PHEASANT_NEST_49937) == null) {
      Movement.walk(Players.getLocal().getWorldLocation());
      return;
    }

    NPC forester = NPCs.getNearest(NpcID.FREAKY_FORESTER_12536);
    if (forester == null) {
      return;
    }

    forester.interact(0);
    Time.sleepTicksUntil(() -> !Inventory.contains(ItemID.PHEASANT_EGG), 10);
  }
}
