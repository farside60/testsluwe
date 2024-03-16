package io.reisub.devious.cooking.tasks;

import io.reisub.devious.cooking.Config;
import io.reisub.devious.cooking.Cooking;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import lombok.RequiredArgsConstructor;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.client.Static;

@RequiredArgsConstructor
public class Drop extends Task {
  private static final WorldPoint HOSIDIUS_COOKING_SPOT = new WorldPoint(1677, 3621, 0);

  private final Cooking plugin;
  private final Config config;

  @Override
  public String getStatus() {
    return "Dropping food";
  }

  @Override
  public boolean validate() {
    int count = Inventory.getCount(config.food());
    return config.sonicMode()
        && (plugin.isCurrentActivity(Activity.IDLE) || count == 1)
        && (count > 0 || plugin.getLastBank() + 1 >= Static.getClient().getTickCount())
        && Static.getClient().getTickCount() >= plugin.getLastDrop() + 3;
  }

  @Override
  public void execute() {
    if (Players.getLocal().distanceTo(HOSIDIUS_COOKING_SPOT) < 10
        && !Players.getLocal().getWorldLocation().equals(HOSIDIUS_COOKING_SPOT)) {
      Movement.walk(HOSIDIUS_COOKING_SPOT);

      if (!Time.sleepTicksUntil(
          () -> Players.getLocal().getWorldLocation().equals(HOSIDIUS_COOKING_SPOT), 10)) {
        return;
      }
    }

    Inventory.getAll(config.food()).forEach((i) -> i.interact("Drop"));

    Time.sleepTick();

    plugin.setLastDrop(Static.getClient().getTickCount());
  }
}
