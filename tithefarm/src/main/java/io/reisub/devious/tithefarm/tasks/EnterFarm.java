package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.DialogOption;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.client.Static;

public class EnterFarm extends Task {
  @Override
  public String getStatus() {
    return "Entering farm";
  }

  @Override
  public boolean validate() {
    return !TitheFarm.isInTitheFarm() && Inventory.contains(Predicates.ids(TitheFarm.SEED_IDS));
  }

  @Override
  public void execute() {
    final TileObject door = TileObjects.getNearest(ObjectID.FARM_DOOR);
    if (door == null) {
      return;
    }

    door.interact("Open");
    Time.sleepTicksUntil(() -> Dialog.isOpen() || Static.getClient().isInInstancedRegion(), 20);

    if (Dialog.isOpen()) {
      Dialog.invokeDialog(DialogOption.NPC_CONTINUE, DialogOption.CHAT_OPTION_THREE);
      Time.sleepTicksUntil(() -> Static.getClient().isInInstancedRegion(), 10);
    }

    Time.sleepTicks(2);
  }
}
