package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.Config;
import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.client.Static;

public class Chop extends Task {
  @Inject private FishingTrawler plugin;
  @Inject private Config config;

  private NPC tentacle;

  @Override
  public String getStatus() {
    return "Chopping";
  }

  @Override
  public boolean validate() {
    if (config.stopAtFifty() && plugin.isEnoughForReward()) {
      return false;
    }

    if (!plugin.isCurrentActivity(Activity.IDLE)) {
      return false;
    }

    final int ticksSinceLastConstruction =
        Static.getClient().getTickCount() - plugin.getLastConstructionTick();

    if (ticksSinceLastConstruction < 15) {
      return false;
    }

    tentacle = NPCs.getNearest(n -> n.hasAction("Chop"));

    return tentacle != null;
  }

  @Override
  public void execute() {
    tentacle.interact("Chop");

    Time.sleepTicksUntil(() -> plugin.isCurrentActivity(FishingTrawler.WOODCUTTING), 5);
  }
}
