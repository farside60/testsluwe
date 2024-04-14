package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Compost;
import io.reisub.devious.farming.Config;
import io.reisub.devious.farming.Farming;
import io.reisub.devious.farming.Location;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.SluweInventory;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class WithdrawTools extends Task {
  @Inject private Farming plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Withdrawing tools";
  }

  @Override
  public boolean validate() {
    NPC leprechaun = NPCs.getNearest("Tool Leprechaun");

    boolean hasAll =
        config.barbarianFarming()
            ? SluweInventory.hasAllItemsInventoryOrEquipped(ItemID.SPADE, ItemID.MAGIC_SECATEURS)
            : SluweInventory.hasAllItemsInventoryOrEquipped(
                ItemID.SEED_DIBBER, ItemID.SPADE, ItemID.MAGIC_SECATEURS);

    return !plugin.getLocationQueue().isEmpty()
        && !hasAll
        && leprechaun != null;
  }

  @Override
  public void execute() {
    if (plugin.getCurrentLocation() == Location.FARMING_GUILD && !config.limpwurt()) {
      SluweMovement.walkTo(new WorldPoint(1238, 3730, 0), 1);
    }

    NPC leprechaun = NPCs.getNearest("Tool Leprechaun");

    leprechaun.interact("Exchange");
    Time.sleepTicksUntil(() -> Widgets.isVisible(Constants.TOOLS_WIDGET.get()), 30);
    Time.sleepTick();

    Constants.TOOLS_WITHDRAW_SECATEURS_WIDGET.get().interact(0);
    if (!config.barbarianFarming()) {
      Constants.TOOLS_WITHDRAW_DIBBER_WIDGET.get().interact(0);
    }
    Constants.TOOLS_WITHDRAW_SPADE_WIDGET.get().interact(0);

    if (config.compost() == Compost.BOTTOMLESS) {
      config.compost().getWidget().interact(0);
    } else {
      if (config.limpwurt()) {
        config.compost().getWidget().interact(0);
      }
      config.compost().getWidget().interact(0);
    }

    Constants.TOOLS_CLOSE_WIDGET.get().interact("Close");
    Time.sleepTicksUntil(() -> Inventory.contains(ItemID.SPADE), 5);

    if (Inventory.contains(ItemID.MAGIC_SECATEURS)) {
      Inventory.getFirst(ItemID.MAGIC_SECATEURS).interact("Wield");
    }
  }
}
