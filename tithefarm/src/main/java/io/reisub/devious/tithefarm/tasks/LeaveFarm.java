package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.tithefarm.Config;
import io.reisub.devious.tithefarm.Reward;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class LeaveFarm extends Task {
  @Inject private TitheFarm plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Leaving farm";
  }

  @Override
  public boolean validate() {
    return TitheFarm.isInTitheFarm()
        && (Inventory.getCount(true, Predicates.ids(TitheFarm.SEED_IDS)) < 25
            || Reward.shouldLeaveFarmToBuyAnything(config)
            || plugin.shouldGetBetterSeeds());
  }

  @Override
  public void execute() {
    final TileObject door = TileObjects.getNearest(ObjectID.FARM_DOOR);
    if (door == null) {
      return;
    }

    door.interact("Open");
    Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);

    Time.sleepTicks(2);
  }
}
