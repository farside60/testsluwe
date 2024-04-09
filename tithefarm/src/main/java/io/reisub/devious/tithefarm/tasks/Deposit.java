package io.reisub.devious.tithefarm.tasks;

import com.google.common.collect.ImmutableSet;
import io.reisub.devious.tithefarm.Config;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

public class Deposit extends Task {
  @Inject private TitheFarm plugin;
  @Inject private Config config;
  private final Set<Integer> fruitIds =
      ImmutableSet.of(ItemID.GOLOVANOVA_FRUIT, ItemID.BOLOGANO_FRUIT, ItemID.LOGAVANO_FRUIT);

  @Override
  public String getStatus() {
    return "Depositing fruit";
  }

  @Override
  public boolean validate() {
    if (plugin.isStartedRun()) {
      return false;
    }

    if (Inventory.getCount(true, Predicates.ids(fruitIds)) >= config.depositAmount()) {
      return true;
    }

    return Inventory.getCount(true, Predicates.ids(TitheFarm.SEED_IDS)) < 25
        && Inventory.contains(Predicates.ids(fruitIds));
  }

  @Override
  public void execute() {
    if (Movement.getRunEnergy() < 80 && Movement.isRunEnabled()) {
      Movement.toggleRun();
    }

    final TileObject sack = TileObjects.getNearest(ObjectID.SACK_27431);
    if (sack == null) {
      return;
    }

    sack.interact("Deposit");
    Time.sleepTicksUntil(() -> !Inventory.contains(Predicates.ids(fruitIds)), 100);

    if (Inventory.getCount(true, Predicates.ids(TitheFarm.SEED_IDS)) >= 25) {
      final WorldPoint destination = Utils.worldToInstance(new WorldPoint(1820, 3486, 0));
      Movement.walk(destination);
    }
  }
}
