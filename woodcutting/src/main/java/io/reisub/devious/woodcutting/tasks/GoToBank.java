package io.reisub.devious.woodcutting.tasks;

import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Location;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.client.Static;

public class GoToBank extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to bank";
  }

  @Override
  public boolean validate() {
    return Inventory.isFull()
        && config.location().getBankPoint() != null
        && !plugin.isDoingForestry()
        && !config.drop()
        && !config.burn()
        && Players.getLocal().distanceTo(config.location().getBankPoint()) >= 10;
  }

  @Override
  public void execute() {
    if (config.location() == Location.HARDWOOD_FOSSIL_ISLAND
        && Skills.getBoostedLevel(Skill.AGILITY) >= 70) {
      final TileObject holeNorth = TileObjects.getNearest(ObjectID.HOLE_31482);

      if (holeNorth != null && Players.getLocal().getWorldLocation().getWorldY() > 3825) {
        GameThread.invoke(() -> holeNorth.interact("Climb through"));
        Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().getWorldY() < 3825, 20);
      }

      if (config.location().getBankPoint().isInScene(Static.getClient())) {
        Movement.walk(config.location().getBankPoint());
        Time.sleepTicksUntil(
            () -> Players.getLocal().distanceTo(config.location().getBankPoint()) < 8, 20);
      } else {
        Movement.walkTo(config.location().getBankPoint());
      }
    } else {
      SluweMovement.walkTo(config.location().getBankPoint());
    }
  }
}
