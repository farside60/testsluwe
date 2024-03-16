package io.reisub.devious.cooking.tasks;

import io.reisub.devious.cooking.Config;
import io.reisub.devious.cooking.Cooking;
import io.reisub.devious.utils.tasks.Task;
import lombok.RequiredArgsConstructor;
import net.runelite.api.Item;
import net.runelite.api.ObjectID;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;

@RequiredArgsConstructor
public class SonicCook extends Task {
  private final Cooking plugin;
  private final Config config;

  @Override
  public String getStatus() {
    return "Cooking";
  }

  @Override
  public boolean validate() {
    return config.sonicMode()
        && TileItems.getFirstAt(Players.getLocal().getWorldLocation(), config.food()) != null;
  }

  @Override
  public void execute() {
    TileItem food = TileItems.getFirstAt(Players.getLocal().getWorldLocation(), config.food());
    if (food == null) {
      return;
    }

    food.interact("Take");
    Time.sleepTick();

    TileObject oven = TileObjects.getNearest(ObjectID.CLAY_OVEN_21302, ObjectID.RANGE_31631);
    TileObject fire = TileObjects.getNearest("Fire");
    if (oven == null && fire == null) {
      return;
    }

    if (oven != null) {
      oven.interact(0);
    } else {
      Item rawFood = Inventory.getFirst(config.food());
      if (rawFood == null) {
        return;
      }

      rawFood.useOn(fire);
    }

    Time.sleepTick();
  }
}
