package io.reisub.devious.autopickup.tasks;

import io.reisub.devious.autopickup.AutoPickup;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.entities.Players;

public class GoToSpot extends Task {
  @Inject private AutoPickup plugin;

  @Override
  public String getStatus() {
    return "Going to spot";
  }

  @Override
  public boolean validate() {
    return plugin.getNearestItem() == null
        && Players.getLocal().distanceTo(plugin.getStartLocation()) > 20;
  }

  @Override
  public void execute() {
    SluweMovement.walkTo(plugin.getStartLocation());
  }
}
