package io.reisub.devious.fishingtrawler.tasks;

import io.reisub.devious.fishingtrawler.FishingTrawler;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;

public class GoUp extends Task {
  @Override
  public String getStatus() {
    return "Going up";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(FishingTrawler.BOAT_REGION) && Players.getLocal().getPlane() == 0;
  }

  @Override
  public void execute() {
    final TileObject ladder = TileObjects.getFirstAt(new WorldPoint(1884, 4826, 0), 4060);
    if (ladder == null) {
      return;
    }

    Time.sleepTick();
    ladder.interact("Climb-up");
    if (!Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), 3)) {
      return;
    }

    Time.sleepTicksUntil(() -> Players.getLocal().getPlane() == 1, 10);
  }
}
