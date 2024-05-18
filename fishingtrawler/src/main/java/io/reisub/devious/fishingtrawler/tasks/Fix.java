package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.Config;
import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;

public class Fix extends Task {
  @Inject private FishingTrawler plugin;
  @Inject private Config config;

  private TileObject rail;

  @Override
  public String getStatus() {
    return "Fixing rail";
  }

  @Override
  public boolean validate() {
    if (config.stopAtFifty() && plugin.isEnoughForReward()) {
      return false;
    }

    if (plugin.isCurrentActivity(FishingTrawler.FIXING_RAIL)) {
      return false;
    }

    if (Utils.isInRegion(FishingTrawler.PORT_REGION)) {
      return false;
    }

    rail = TileObjects.getNearest(o -> o.hasAction("Fix"));

    return rail != null;
  }

  @Override
  public void execute() {
    rail.interact("Fix");

    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(FishingTrawler.FIXING_RAIL), 5);
  }
}
