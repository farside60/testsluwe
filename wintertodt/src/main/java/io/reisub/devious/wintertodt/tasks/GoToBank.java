package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Inventory;

public class GoToBank extends Task {
  @Inject private Wintertodt plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Going to the bank";
  }

  @Override
  public boolean validate() {
    if (!plugin.isInWintertodtRegion()) {
      return false;
    }

    // Start moving to bank when we have no more food, and we're at the eating threshold
    if (!Inventory.contains(i -> i.hasAction("Eat", "Drink"))
        && ((config.checkMissing() && Combat.getMissingHealth() >= config.eatThreshold())
            || (!config.checkMissing() && Combat.getCurrentHealth() <= config.eatThreshold()))) {
      return true;
    }

    // Start moving to bank when nearing the end of the round and there's nothing left to do
    return Inventory.getCount(i -> i.hasAction("Eat", "Drink")) <= 1
        && (plugin.getRespawnTimer() > 0
            || (plugin.getBossHealth() <= 4
                && !Inventory.contains(ItemID.BRUMA_ROOT, ItemID.BRUMA_KINDLING)));
  }

  @Override
  public void execute() {
    final TileObject doorsOfDinh = TileObjects.getNearest(ObjectID.DOORS_OF_DINH);
    if (doorsOfDinh == null) {
      return;
    }

    final WorldPoint target = new WorldPoint(1630, 3975, 0);

    if (Players.getLocal().getWorldLocation().getY() > doorsOfDinh.getY()
        && Players.getLocal().distanceTo(doorsOfDinh) > 8) {
      SluweMovement.walkTo(target, 3);
      Time.sleepTick();
    }

    Time.sleepTicksUntil(() -> !plugin.bossIsUp(), 200);

    doorsOfDinh.interact("Enter");
    Time.sleepTicksUntil(() -> Players.getLocal().getWorldLocation().getY() < 3965, 20);

    final WorldPoint bank = new WorldPoint(1639, 3944, 0);
    SluweMovement.walkTo(bank);
  }
}
