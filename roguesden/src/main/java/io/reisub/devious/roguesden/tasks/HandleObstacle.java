package io.reisub.devious.roguesden.tasks;

import io.reisub.devious.roguesden.Obstacle;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.movement.Movement;

public class HandleObstacle extends Task {
  private Obstacle current;
  private Obstacle next;

  @Override
  public String getStatus() {
    return "Handling obstacle: " + current;
  }

  @Override
  public boolean validate() {
    current = null;
    next = null;

    for (Obstacle obstacle : Obstacle.values()) {
      if (current != null) {
        next = obstacle;
        return true;
      }

      if (Players.getLocal().getWorldLocation().equals(obstacle.getStart())) {
        current = obstacle;
      }
    }

    return false;
  }

  @Override
  public void execute() {
    if (Movement.isRunEnabled()) {
      if (!current.isRun() && Movement.getRunEnergy() < 50) {
        Movement.toggleRun();
      }
    } else {
      if (current.isRun() || Movement.getRunEnergy() > 70) {
        Movement.toggleRun();
      }
    }

    if (current.getObstacleObject() != null) {
      TileObject obstacle =
          TileObjects.getFirstAt(
              current.getObstacleObject().getLocation(),
              o -> o.hasAction(current.getObstacleObject().getAction()));

      if (obstacle == null) {
        return;
      }

      obstacle.interact(current.getObstacleObject().getAction());
    } else {
      if (current == Obstacle.WALK_SEVEN) {
        SluweMovement.walkTo(next.getStart(), () -> {
          if (Movement.getRunEnergy() < 50 && Movement.isRunEnabled()) {
            Movement.toggleRun();
          }
        });

        if (!Players.getLocal().getWorldLocation().equals(next.getStart())) {
          Movement.walk(next.getStart());
        }
      } else if (current != Obstacle.BLADE_ONE) {
        Movement.walk(next.getStart());
      }
    }

    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().equals(next.getStart()), 30);

    if (current.getWaitTicks() > 0) {
      Time.sleepTicks(current.getWaitTicks());
    }
  }
}
