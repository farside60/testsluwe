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
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class Pheasants extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Stealing egg";
  }

  @Override
  public boolean validate() {
    return config.forestryPheasants() && NPCs.getNearest(NpcID.FREAKY_FORESTER_12536) != null;
  }

  @Override
  public void execute() {
    plugin.setActivity(Woodcutting.FORESTRY);

    TileObject nest = TileObjects.getNearest(ObjectID.PHEASANT_NEST_49937);
    if (nest == null) {
      return;
    }

    if (Inventory.isFull()) {
      Inventory.getFirst(i -> i.getName().contains("logs")).drop();
      Time.sleepTick();
    }

    nest.interact(0);
    Time.sleepTicksUntil(() -> Inventory.contains(ItemID.PHEASANT_EGG), 10);

    NPC forester = NPCs.getNearest(NpcID.FREAKY_FORESTER_12536);
    if (forester == null) {
      return;
    }

    forester.interact(0);
    Time.sleepTicksUntil(() -> !Inventory.contains(ItemID.PHEASANT_EGG), 10);
  }
}
