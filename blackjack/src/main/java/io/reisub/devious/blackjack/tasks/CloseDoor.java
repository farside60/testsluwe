package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;

public class CloseDoor extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Closing door";
  }

  @Override
  public boolean validate() {
    return config.target().getRoom().contains(Players.getLocal())
        && config.target().getRoom().getDoor().hasAction("Close");
  }

  @Override
  public void execute() {
    TileObject door = config.target().getRoom().getDoor();
    if (door == null) {
      return;
    }

    door.interact("Close");
    Time.sleepTicksUntil(() -> config.target().getRoom().getDoor().hasAction("Open"), 5);
  }
}
