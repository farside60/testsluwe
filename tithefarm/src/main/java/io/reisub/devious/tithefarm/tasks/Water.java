package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Patch;
import io.reisub.devious.tithefarm.PatchState;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import java.util.Comparator;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class Water extends Task {
  @Inject private TitheFarm plugin;
  private Patch currentPatch;

  @Override
  public String getStatus() {
    return "Watering";
  }

  @Override
  public boolean validate() {
    if (!plugin.isStartedRun() || !TitheFarm.isInTitheFarm()) {
      return false;
    }

    currentPatch = Patch.getCurrent();

    return currentPatch != null && currentPatch.getState() == PatchState.UNWATERED;
  }

  @Override
  public void execute() {
    final Item wateringCan = getWateringCan();
    if (wateringCan == null) {
      return;
    }

    wateringCan.useOn(currentPatch.getObject());

    if (plugin.isFinishedPlanting()) {
      Time.sleepTicks(3);
    } else {
      Time.sleepTicks(2);
    }

    if (Patch.isAtEnd()) {
      plugin.setFinishedPlanting(true);
    }

    Patch.takeStep();

    if (Inventory.contains(ItemID.GRICOLLERS_CAN)) {
      plugin.setGricollersFull(false);
    }
  }

  /**
   * This returns a watering can different from the last used one. This is necessary for animation
   * canceling. We do this by returning the most full can in the inventory.
   *
   * @return fullest watering can
   */
  private Item getWateringCan() {
    if (Inventory.contains(ItemID.GRICOLLERS_CAN)) {
      return Inventory.getFirst(ItemID.GRICOLLERS_CAN);
    }

    List<Item> cans = Inventory.getAll(i -> i.getName().startsWith("Watering can"));
    if (cans.isEmpty()) {
      return null;
    }

    // sort by id, highest ID is the fullest can
    cans.sort(Comparator.comparingInt(Item::getId).reversed());

    return cans.get(0);
  }
}
