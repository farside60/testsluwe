package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Wintertodt;
import java.time.Duration;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Movement;

public class ChangeSide extends Task {
  @Inject public Wintertodt plugin;
  @Inject public Config config;

  private Instant lastIncap;

  @Override
  public String getStatus() {
    return "Changing side";
  }

  @Override
  public boolean validate() {
    if (!plugin.bossIsUp()) {
      lastIncap = null;
      return false;
    }

    boolean incapPyroFound =
        NPCs.getNearest(
                npc ->
                    npc.getId() == NpcID.INCAPACITATED_PYROMANCER
                        && npc.distanceTo(Players.getLocal()) <= 6)
            != null;

    boolean nearBurningBrazier =
        TileObjects.getFirstSurrounding(
                Players.getLocal().getWorldLocation(), 8, "Burning brazier")
            != null;

    if (incapPyroFound && !nearBurningBrazier) {
      if (lastIncap == null) {
        lastIncap = Instant.now();
      }
    } else {
      lastIncap = null;
      return false;
    }

    return lastIncap != null
        && Duration.between(lastIncap, Instant.now()).getSeconds() > config.sideTimeout()
        && plugin.isCurrentActivity(Activity.IDLE);
  }

  @Override
  public void execute() {
    lastIncap = null;

    final WorldPoint nearBrazier = plugin.getFurthestSide().getPositionNearBrazier();;

    Movement.walk(nearBrazier);
    if (!Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), 3)) {
      return;
    }

    Time.sleepTicksUntil(() -> !Players.getLocal().isMoving()
        || Players.getLocal().getWorldLocation().equals(nearBrazier), 20);
  }
}
