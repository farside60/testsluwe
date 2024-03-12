package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class Fix extends Task {
  @Inject public Wintertodt plugin;

  private TileObject brazier;

  @Override
  public String getStatus() {
    return "Fixing brazier";
  }

  @Override
  public boolean validate() {
    if (!plugin.bossIsUp() || plugin.isCurrentActivity(Wintertodt.FIXING_BRAZIER)) {
      return false;
    }

    int distance = plugin.isCurrentActivity(Activity.IDLE) ? 8 : 2;

    brazier =
        TileObjects.getFirstSurrounding(
            Players.getLocal().getWorldLocation(), distance, o -> o.hasAction("Fix"));

    return brazier != null;
  }

  @Override
  public void execute() {
    brazier.interact("Fix");

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
            plugin.isCurrentActivity(Wintertodt.FIXING_BRAZIER)
                || TileObjects.getFirstSurrounding(
                        Players.getLocal().getWorldLocation(), distance, o -> o.hasAction("Fix"))
                    == null,
        timeout);
  }
}
