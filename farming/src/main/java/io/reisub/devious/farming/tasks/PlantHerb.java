package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Config;
import io.reisub.devious.farming.Farming;
import io.reisub.devious.farming.Location;
import io.reisub.devious.farming.PatchImplementation;
import io.reisub.devious.farming.PatchState;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.ConfigList;
import io.reisub.devious.utils.api.SluwePredicates;
import io.reisub.devious.utils.tasks.Task;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.TileObject;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;

public class PlantHerb extends Task {
  @Inject private Farming plugin;

  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Planting herb seeds";
  }

  @Override
  public boolean validate() {
    if (Inventory.isFull() || !Inventory.contains(Predicates.ids(Constants.HERB_SEED_IDS))) {
      return false;
    }

    final TileObject patch = TileObjects.getNearest(Predicates.ids(Constants.HERB_PATCH_IDS));

    if (patch == null) {
      return false;
    }

    final int varbitValue = Vars.getBit(plugin.getCurrentLocation().getHerbVarbit());
    final PatchState patchState = PatchImplementation.HERB.forVarbitValue(varbitValue);

    return patchState != null
        && patchState.getProduce() == Produce.WEEDS
        && patchState.getCropState() == CropState.GROWING;
  }

  @Override
  public void execute() {
    final TileObject patch = TileObjects.getNearest(Predicates.ids(Constants.HERB_PATCH_IDS));
    if (patch == null) {
      return;
    }

    final List<Item> seeds = Inventory.getAll(Predicates.ids(Constants.HERB_SEED_IDS));
    if (seeds == null || seeds.isEmpty()) {
      return;
    }

    final ConfigList diseaseFreeSeedsList = ConfigList.parseList(config.diseaseFreeSeeds());
    final List<Item> diseaseFreeSeeds =
        Inventory.getAll(SluwePredicates.itemConfigList(diseaseFreeSeedsList));
    final Location currentLocation = plugin.getCurrentLocation();

    Item seed = null;

    if (!diseaseFreeSeeds.isEmpty()) {
      if (currentLocation == Location.TROLL_STRONGHOLD || currentLocation == Location.WEISS) {
        seed = diseaseFreeSeeds.get(0);
      } else {
        for (Item s : seeds) {
          if (!diseaseFreeSeeds.contains(s)) {
            seed = s;
            break;
          }
        }

        if (seed == null) {
          seed = seeds.get(0);
        }
      }
    } else {
      seed = seeds.get(0);
    }

    if (seed == null) {
      return;
    }

    seed.useOn(patch);

    if (!Time.sleepTicksUntil(() -> Vars.getBit(currentLocation.getHerbVarbit()) > 3, 20)) {
      return;
    }

    final Item compost = Inventory.getFirst(Predicates.ids(Constants.COMPOST_IDS));
    if (compost == null) {
      return;
    }

    compost.useOn(patch);
    Time.sleepTicks(3);
  }
}
