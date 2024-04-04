package io.reisub.devious.autoflinch.tasks;

import io.reisub.devious.autoflinch.Autoflinch;
import io.reisub.devious.autoflinch.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;

public class Attack extends Task {
  @Inject private Autoflinch plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Attacking";
  }

  @Override
  public boolean validate() {
    if (plugin.getSafeLocation() == null || plugin.getTarget() == null) {
      return false;
    }

    if (plugin.getTarget().isDead()) {
      plugin.stop("Target is dead, stopping Autoflinch.");
      return false;
    }

    return plugin.getSafeLocation().equals(Players.getLocal().getWorldLocation())
        && plugin.getTarget().getHealthScale() == -1;
  }

  @Override
  public void execute() {
    plugin.getTarget().interact("Attack");
    Time.sleepTicks(config.waitTicks());

    Movement.walk(plugin.getSafeLocation());
    Time.sleepTicksUntil(
        () -> Players.getLocal().getWorldLocation().equals(plugin.getSafeLocation()), 3);
  }
}
