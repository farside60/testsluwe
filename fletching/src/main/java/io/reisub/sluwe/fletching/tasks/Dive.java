package io.reisub.sluwe.fletching.tasks;

import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.sluwe.fletching.Config;
import io.reisub.sluwe.fletching.Fletching;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.widgets.Dialog;

public class Dive extends Task {
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Diving";
  }

  @Override
  public boolean validate() {
    return config.farmSeaweedSpores()
        && Utils.isInRegion(Fletching.FOSSIL_ISLAND_SMALL_ISLAND_REGION);
  }

  @Override
  public void execute() {
    if (Dialog.isOpen()) {
      Dialog.close();
    }

    final TileObject rowboat = TileObjects.getNearest(ObjectID.ROWBOAT_30919);
    if (rowboat == null) {
      return;
    }

    rowboat.interact("Dive");
    Time.sleepTicksUntil(
        () -> Dialog.isOpen() || Utils.isInRegion(Fletching.FOSSIL_ISLAND_SEAWEED_REGION), 10);

    if (Dialog.isOpen()) {
      Dialog.chooseOption(1);
      Time.sleepTicksUntil(() -> Utils.isInRegion(Fletching.FOSSIL_ISLAND_SEAWEED_REGION), 10);
    }

    Time.sleepTicks(2);
  }
}
