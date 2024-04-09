package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.client.Static;

public class TakeSeeds extends Task {
  @Inject private TitheFarm plugin;

  @Override
  public String getStatus() {
    return "Taking seeds";
  }

  @Override
  public boolean validate() {
    return !Static.getClient().isInInstancedRegion()
        && Utils.isInRegion(TitheFarm.TITHE_FARM_REGION)
        && !Inventory.contains(Predicates.ids(TitheFarm.SEED_IDS));
  }

  @Override
  public void execute() {
    final TileObject table = TileObjects.getNearest(ObjectID.SEED_TABLE);
    if (table == null) {
      return;
    }

    table.interact("Search");
    Time.sleepTicksUntil(Dialog::isOpen, 20);

    final int farmingLevel = Skills.getLevel(Skill.FARMING);

    if (farmingLevel >= 74) {
      Dialog.chooseOption(3);
    } else if (farmingLevel >= 54) {
      Dialog.chooseOption(2);
    } else if (farmingLevel >= 34) {
      Dialog.chooseOption(1);
    } else {
      plugin.stop("Farming level too low, stopping plugin");
      return;
    }

    Time.sleepTicksUntil(Dialog::isEnterInputOpen, 5);
    Dialog.enterAmount(10000);

    Time.sleepTicksUntil(() -> Inventory.contains(Predicates.ids(TitheFarm.SEED_IDS)), 5);
    Dialog.close();
  }
}
