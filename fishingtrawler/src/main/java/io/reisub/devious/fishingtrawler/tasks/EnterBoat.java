package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class EnterBoat extends Task {
  @Inject private FishingTrawler plugin;

  private final WorldPoint destination = new WorldPoint(2672, 3170, 1);

  @Override
  public String getStatus() {
    return "Entering boat";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(FishingTrawler.PORT_REGION)
        && plugin.isTakenReward()
        && !Players.getLocal().getWorldLocation().equals(destination);
  }

  @Override
  public void execute() {
    final TileObject gangplank =
        TileObjects.getFirstAt(new WorldPoint(2675, 3170, 0), ObjectID.GANGPLANK_4977);
    if (gangplank == null) {
      return;
    }

    gangplank.interact("Cross");
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(destination), 25);
  }
}
