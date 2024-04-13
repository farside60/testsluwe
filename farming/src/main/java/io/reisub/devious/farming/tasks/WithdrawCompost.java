package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Config;
import io.reisub.devious.farming.Farming;
import io.reisub.devious.farming.PatchImplementation;
import io.reisub.devious.farming.PatchState;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.client.plugins.timetracking.farming.CropState;
import net.runelite.client.plugins.timetracking.farming.Produce;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class WithdrawCompost extends Task {
  @Inject private Farming plugin;
  @Inject private Config config;
  private NPC leprechaun;

  @Override
  public String getStatus() {
    return "Withdrawing compost";
  }

  @Override
  public boolean validate() {
    leprechaun = NPCs.getNearest("Tool Leprechaun");

    final int varbitValue = Vars.getBit(plugin.getCurrentLocation().getHerbVarbit());
    final PatchState patchState = PatchImplementation.HERB.forVarbitValue(varbitValue);

    return plugin.getCurrentLocation() != null
        && leprechaun != null
        && Utils.isInRegion(plugin.getCurrentLocation().getRegionId())
        && !Inventory.contains(config.compost().getId())
        && patchState != null
        && patchState.getProduce() != Produce.WEEDS
        && (patchState.getCropState() == CropState.HARVESTABLE
            || patchState.getCropState() == CropState.DEAD);
  }

  @Override
  public void execute() {
    leprechaun.interact("Exchange");
    Time.sleepTicksUntil(() -> Widgets.isVisible(Constants.TOOLS_WIDGET.get()), 30);
    Time.sleepTick();

    if (Inventory.contains(ItemID.BUCKET)) {
      Constants.TOOLS_DEPOSIT_BUCKET_WIDGET.get().interact("Store-All");
    }

    if (config.limpwurt()) {
      config.compost().getWidget().interact(0);
    }
    config.compost().getWidget().interact(0);

    Constants.TOOLS_CLOSE_WIDGET.get().interact("Close");
    Time.sleepTicksUntil(() -> Inventory.contains(config.compost().getId()), 5);
  }
}
