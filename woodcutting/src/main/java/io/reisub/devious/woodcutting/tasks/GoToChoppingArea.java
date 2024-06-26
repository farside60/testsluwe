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
import net.unethicalite.client.Static;

public class GoToChoppingArea extends Task {

  @Inject private Woodcutting plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to chopping area";
  }

  @Override
  public boolean validate() {
    return !plugin.isDoingForestry()
        && (!Inventory.isFull()
            || Static.getClient().getTickCount() - plugin.getLastBankTick() <= 2)
        && Players.getLocal().distanceTo(config.location().getWoodcuttingAreaPoint())
            > config.location().getWoodcuttingAreaRadius();
  }

  @Override
  public void execute() {
    if (config.location() == Location.HARDWOOD_FOSSIL_ISLAND
        && Skills.getBoostedLevel(Skill.AGILITY) >= 70) {
      final TileObject holeSouth = TileObjects.getNearest(ObjectID.HOLE_31481);

      if (holeSouth != null && Players.getLocal().getWorldLocation().getWorldY() < 3825) {
        GameThread.invoke(() -> holeSouth.interact("Climb through"));
        Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().getWorldY() > 3825, 30);
      } else {
        SluweMovement.walkTo(config.location().getWoodcuttingAreaPoint(), 1);
      }
    } else {
      SluweMovement.walkTo(config.location().getWoodcuttingAreaPoint(), 1);
    }

    if (config.location() == Location.WILLOWS_DRAYNOR) {
      Time.sleepTicks(5);
    }
  }
}
