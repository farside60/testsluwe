package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.Config;
import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.client.Static;

public class GoToTentacle extends Task {
  @Inject private FishingTrawler plugin;
  @Inject private Config config;

  private int lastSwap;

  @Override
  public String getStatus() {
    return "Going to tentacle";
  }

  @Override
  public boolean validate() {
    if (Static.getClient().getTickCount() - lastSwap < 10) {
      return false;
    }

    if (!plugin.isCurrentActivity(Activity.IDLE)) {
      return false;
    }

    if (config.stopAtFifty() && plugin.isEnoughForReward()) {
      return false;
    }

    final WorldPoint current = Players.getLocal().getWorldLocation();

    if ((!Utils.isInRegion(FishingTrawler.BOAT_REGION)
            && !Utils.isInRegion(FishingTrawler.BOAT_REGION2))
        || current.getPlane() != 1) {
      return false;
    }

    if (!current.equals(getNorth()) && !current.equals(getSouth())) {
      return true;
    }

    if (shouldSwitch()) {
      return true;
    }

    final NPC tentacle = NPCs.getNearest(n -> n.hasAction("Chop"));
    final int ticksSinceLastConstruction =
        Static.getClient().getTickCount() - plugin.getLastConstructionTick();

    if (plugin.getRailingsFixed() == 2) {
      return true;
    }

    return tentacle == null && ticksSinceLastConstruction > 8 && ticksSinceLastConstruction <= 10;
  }

  @Override
  public void execute() {
    lastSwap = Static.getClient().getTickCount();

    if (plugin.getRailingsFixed() == 2) {
      Time.sleepTick();
    }

    plugin.setRailingsFixed(0);

    final WorldPoint destination =
        Players.getLocal().getWorldLocation().equals(getNorth()) ? getSouth() : getNorth();

    Movement.walk(destination);
    Time.sleepTick();

    Time.sleepTicksUntil(
        () ->
            Players.getLocal().getWorldLocation().equals(getNorth())
                || Players.getLocal().getWorldLocation().equals(getSouth()),
        10);
  }

  private WorldPoint getNorth() {
    if (Utils.isInRegion(FishingTrawler.BOAT_REGION)) {
      return new WorldPoint(1885, 4827, 1);
    } else {
      return new WorldPoint(2013, 4827, 1);
    }
  }

  private WorldPoint getSouth() {
    if (Utils.isInRegion(FishingTrawler.BOAT_REGION)) {
      return new WorldPoint(1885, 4823, 1);
    } else {
      return new WorldPoint(2013, 4823, 1);
    }
  }

  private boolean shouldSwitch() {
    final NPC tentacle = NPCs.getNearest(n -> n.hasAction("Chop"));

    if (tentacle == null) {
      return false;
    }

    final WorldPoint current = Players.getLocal().getWorldLocation();

    if (getNorth().distanceTo(tentacle) < getSouth().distanceTo(tentacle)) {
      return current.equals(getSouth());
    } else {
      return current.equals(getNorth());
    }
  }
}
