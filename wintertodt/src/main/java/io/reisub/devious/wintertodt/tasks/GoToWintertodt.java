package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Side;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

public class GoToWintertodt extends Task {
  @Inject public Wintertodt plugin;
  @Inject public Config config;

  @Override
  public String getStatus() {
    return "Going to Wintertodt area";
  }

  @Override
  public boolean validate() {
    return !plugin.isInWintertodtRegion() && Inventory.contains(i -> i.hasAction("Eat", "Drink"));
  }

  @Override
  public void execute() {
    final TileObject doorsOfDinh = TileObjects.getNearest(ObjectID.DOORS_OF_DINH);
    if (doorsOfDinh == null) {
      return;
    }

    final WorldPoint target = new WorldPoint(1630, 3962, 0);

    if (Players.getLocal().getWorldLocation().getY() < doorsOfDinh.getY()
        && Players.getLocal().distanceTo(doorsOfDinh) > 7) {
      SluweMovement.walkTo(target, 3);
    }

    doorsOfDinh.interact("Enter");
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().getY() > 3965, 20);

    Side side = config.sideSelection();

    if (plugin.getBossHealth() > 0) {
      TileObject roots = TileObjects.getNearest(ObjectID.BRUMA_ROOTS);
      if (roots == null) {
        return;
      }

      roots.interact("Chop");
    } else {
      final WorldPoint wintertodtSpot =
          plugin.getRespawnTimer() >= 5
              ? side.getPositionNearBrazier().getWorldLocation()
              : side.getPositionNearRoots().getWorldLocation();

      SluweMovement.walkTo(wintertodtSpot);
    }
  }
}
