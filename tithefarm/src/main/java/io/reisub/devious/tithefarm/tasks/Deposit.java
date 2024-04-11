package io.reisub.devious.tithefarm.tasks;

import com.google.common.collect.ImmutableSet;
import io.reisub.devious.tithefarm.Config;
import io.reisub.devious.tithefarm.TitheFarm;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Item;
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
  private final Set<Integer> fruitIds =
      ImmutableSet.of(ItemID.GOLOVANOVA_FRUIT, ItemID.BOLOGANO_FRUIT, ItemID.LOGAVANO_FRUIT);
  private final Set<Integer> farmersOutfitIds =
      ImmutableSet.of(
          ItemID.FARMERS_JACKET,
          ItemID.FARMERS_SHIRT,
          ItemID.FARMERS_BORO_TROUSERS,
          ItemID.FARMERS_BORO_TROUSERS_13641,
          ItemID.FARMERS_STRAWHAT,
          ItemID.FARMERS_STRAWHAT_13647,
          ItemID.FARMERS_BOOTS,
          ItemID.FARMERS_BOOTS_13645);
  @Inject private TitheFarm plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Depositing fruit";
  }

  @Override
  public boolean validate() {
    if (plugin.isStartedRun() || !Inventory.contains(Predicates.ids(fruitIds))) {
      return false;
    }

    if (Inventory.getCount(true, Predicates.ids(fruitIds)) >= config.depositAmount()) {
      return true;
    }

    if (plugin.shouldGetBetterSeeds()) {
      return true;
    }

    return Inventory.getCount(true, Predicates.ids(TitheFarm.SEED_IDS)) < 25;
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

    equipOutfit(farmersOutfitIds);

    final int fruitCount = Inventory.getCount(true, Predicates.ids(fruitIds));

    sack.interact("Deposit");
    Time.sleepTicksUntil(
        () -> Inventory.getCount(true, Predicates.ids(fruitIds)) < fruitCount, 100);

    if (Inventory.getCount(true, Predicates.ids(TitheFarm.SEED_IDS)) >= 25) {
      final WorldPoint destination = Utils.worldToInstance(new WorldPoint(1820, 3486, 0));
      Movement.walk(destination);
      Time.sleepTick();
    }

    equipOutfit(Constants.GRACEFUL_SET);
  }

  private void equipOutfit(Set<Integer> outfitIds) {
    List<Item> pieces = Inventory.getAll(Predicates.ids(outfitIds));
    if (pieces.isEmpty()) {
      return;
    }

    for (Item piece : pieces) {
      piece.interact("Wear");
    }

    Time.sleepTick();
  }
}
