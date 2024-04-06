package io.reisub.devious.tempoross.tasks;

import io.reisub.devious.tempoross.Tempoross;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.api.interaction.Interaction;
import io.reisub.devious.utils.api.interaction.checks.CurrentActivityCheck;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.unethicalite.api.entities.NPCs;

public class Attack extends Task {
  @Inject private Tempoross plugin;

  private NPC pool;

  @Override
  public String getStatus() {
    return "Attacking";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInTemporossArea() || !plugin.isCurrentActivity(Activity.IDLE)) {
      return false;
    }

    // don't attack when he's almost dead, but we still have a phase to do
    if (plugin.getPhase() == 3 && plugin.getEssence() <= 10) {
      return false;
    }

    // don't attack in phase 4 when we still have cooked fish
    if (plugin.getPhase() >= 4 && plugin.getCookedFish() > 0) {
      return false;
    }

    pool =
        NPCs.getNearest(
            (n) -> n.getId() == NpcID.SPIRIT_POOL && plugin.getIslandArea().contains(n));

    return pool != null;
  }

  @Override
  public void execute() {
    new Interaction(pool, new CurrentActivityCheck(5, plugin, Activity.ATTACKING)).interact();
  }
}
