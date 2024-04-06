package io.reisub.devious.smelter.tasks;

import io.reisub.devious.smelter.Config;
import io.reisub.devious.smelter.Smelter;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Production;
import net.unethicalite.client.Static;

public class Smelt extends Task {
  @Inject private Smelter plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Smelting";
  }

  @Override
  public boolean validate() {
    if (plugin.isCurrentActivity(Smelter.SMELTING)) {
      return false;
    }

    if (Static.getClient().getTickCount() <= plugin.getLastBankTick() + 3) {
      return true;
    }

    return config.product().hasMaterials();
  }

  @Override
  public void execute() {
    if (!config.location().getFurnaceLocation().isInScene(Static.getClient())) {
      SluweMovement.walkTo(config.location().getFurnaceLocation(), 1);
    }

    final TileObject furnace = TileObjects.getNearest(config.location().getFurnaceId());
    if (furnace == null) {
      return;
    }

    furnace.interact("Smelt");

    if (Dialog.isOpen()) {
      Dialog.close();
    }

    if (!Time.sleepTicksUntil(() -> config.product().hasMaterials(), 3)) {
      return;
    }

    if (!Time.sleepTicksUntil(Production::isOpen, 20)) {
      return;
    }
    
    Production.chooseOption(config.product().getProductionIndex());

    plugin.setActivity(Smelter.SMELTING);
  }
}
