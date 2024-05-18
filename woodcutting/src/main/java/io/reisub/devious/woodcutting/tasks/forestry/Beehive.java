package io.reisub.devious.woodcutting.tasks.forestry;

import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Inventory;

public class Beehive extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Building beehive";
  }

  @Override
  public boolean validate() {
    return config.forestryBeehive()
        && getBeehive() != null
        && Inventory.contains(Predicates.ids(Constants.LOG_IDS));
  }

  @Override
  public void execute() {
    final NPC beehive = getBeehive();
    if (beehive == null) {
      return;
    }

    final WorldPoint beehiveLocation = beehive.getWorldLocation();

    beehive.interact(0);
    Time.sleepTicksUntil(
        () ->
            !Inventory.contains(Predicates.ids(Constants.LOG_IDS))
                || getBeehiveAt(beehiveLocation) == null,
        50);
  }

  private NPC getBeehive() {
    return NPCs.getNearest(NpcID.UNFINISHED_BEEHIVE, NpcID.UNFINISHED_BEEHIVE_12516);
  }

  private NPC getBeehiveAt(WorldPoint location) {
    return NPCs.getNearest(
        n ->
            n.getWorldLocation().equals(location)
                && (n.getId() == NpcID.UNFINISHED_BEEHIVE
                    || n.getId() == NpcID.UNFINISHED_BEEHIVE_12516));
  }
}
