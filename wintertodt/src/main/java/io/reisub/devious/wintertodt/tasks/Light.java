package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Wintertodt;
import java.time.Instant;
import javax.inject.Inject;
import net.runelite.api.NpcID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class Light extends Task {
  @Inject public Wintertodt plugin;

  private TileObject brazier;

  @Override
  public String getStatus() {
    return "Lighting the brazier";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInWintertodtRegion()) {
      return false;
    }

    int distance = plugin.isCurrentActivity(Activity.IDLE) ? 8 : 2;

    brazier =
        TileObjects.getFirstSurrounding(
            Players.getLocal().getWorldLocation(), distance, o -> o.hasAction("Light"));

    return brazier != null
        && (plugin.getBossHealth() > 0 || plugin.getRespawnTimer() == 0)
        && (plugin.getLastWin() == null
            || Instant.now().isAfter(plugin.getLastWin().plusSeconds(3)))
        && !plugin.isCurrentActivity(Wintertodt.LIGHTING_BRAZIER)
        && NPCs.getNearest(
                n ->
                    n.getId() == NpcID.INCAPACITATED_PYROMANCER
                        && Players.getLocal().distanceTo(n) < 7)
            == null;
  }

  @Override
  public void execute() {
    brazier.interact("Light");

    int distance;
    int timeout;

    if (plugin.isCurrentActivity(Activity.IDLE)) {
      distance = 8;
      timeout = 6;
    } else {
      distance = 2;
      timeout = 3;
    }

    Time.sleepTicksUntil(
        () ->
            plugin.isCurrentActivity(Wintertodt.LIGHTING_BRAZIER)
                || TileObjects.getFirstSurrounding(
                        Players.getLocal().getWorldLocation(), distance, o -> o.hasAction("Light"))
                    == null,
        timeout);
  }
}
