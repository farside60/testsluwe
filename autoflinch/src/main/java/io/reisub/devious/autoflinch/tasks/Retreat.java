package io.reisub.devious.autoflinch.tasks;

import io.reisub.devious.autoflinch.Autoflinch;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;

/**
 * Ideally this task should never run. It's used as a fallback in case the Attack task
 * doesn't return us to the safe location.
 */
public class Retreat extends Task {
  @Inject private Autoflinch plugin;

  @Override
  public String getStatus() {
    return "Retreating";
  }

  @Override
  public boolean validate() {
    return plugin.getSafeLocation() != null
        && !Players.getLocal().getWorldLocation().equals(plugin.getSafeLocation());
  }

  @Override
  public void execute() {
    Movement.walk(plugin.getSafeLocation());
    Time.sleepTicksUntil(
        () -> Players.getLocal().getWorldLocation().equals(plugin.getSafeLocation()), 3);
  }
}
