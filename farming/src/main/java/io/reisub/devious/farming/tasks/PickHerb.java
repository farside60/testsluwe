package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Farming;
import io.reisub.devious.farming.PatchImplementation;
import io.reisub.devious.farming.PatchState;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.Interact;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.events.StatChanged;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;

public class PickHerb extends Task {
  @Inject private Farming plugin;

  private boolean experienceReceived;

  @Override
  public String getStatus() {
    return "Picking herbs";
  }

  @Override
  public boolean validate() {
    if (Inventory.isFull()) {
      return false;
    }

    TileObject patch = TileObjects.getNearest(Predicates.ids(Constants.HERB_PATCH_IDS));

    if (patch == null) {
      return false;
    }

    int varbitValue = Vars.getBit(plugin.getCurrentLocation().getHerbVarbit());
    PatchState patchState = PatchImplementation.HERB.forVarbitValue(varbitValue);

    return patchState != null && patchState.getCropState() == CropState.HARVESTABLE;
  }

  @Override
  public void execute() {
    TileObject patch = TileObjects.getNearest(Predicates.ids(Constants.HERB_PATCH_IDS));
    if (patch == null) {
      return;
    }

    experienceReceived = false;
    patch.interact("Pick");
    if (!Time.sleepTicksUntil(() -> experienceReceived, 30)) {
      return;
    }

    patch.interact("Pick");
    Time.sleepTick();
    patch.interact("Pick");

    if (!Interact.waitUntilActive()) {
      return;
    }

    Time.sleepTicksUntil(
        () -> Inventory.isFull() || Vars.getBit(plugin.getCurrentLocation().getHerbVarbit()) <= 3,
        100);

    if (Vars.getBit(plugin.getCurrentLocation().getHerbVarbit()) <= 3) {
      Time.sleepTicks(2);
    }
  }

  @Subscribe
  private void onStatChanged(StatChanged event) {
    if (plugin.isRunning() && event.getSkill() == Skill.FARMING) {
      experienceReceived = true;
    }
  }
}
