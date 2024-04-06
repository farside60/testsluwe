package io.reisub.devious.birdhouse.tasks;

import io.reisub.devious.birdhouse.BirdHouse;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import lombok.AllArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.entities.Players;

@AllArgsConstructor
public class GoToBirdHouse extends Task {
  private final BirdHouse plugin;
  private final WorldPoint target = new WorldPoint(3680, 3815, 0);

  @Override
  public String getStatus() {
    return "Going to southern bird house";
  }

  @Override
  public boolean validate() {
    return plugin.isEmptied(Constants.MEADOW_NORTH_SPACE)
        && !plugin.isEmptied(Constants.MEADOW_SOUTH_SPACE)
        && Players.getLocal().distanceTo(target) > 15;
  }

  @Override
  public void execute() {
    SluweMovement.walkTo(target);
  }
}
