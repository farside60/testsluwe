package io.reisub.devious.farming.tasks;

import io.reisub.devious.farming.Farming;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.SluweInventory;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class DepositTools extends Task {
  @Inject private Farming plugin;

  @Override
  public String getStatus() {
    return "Depositing tools";
  }

  @Override
  public boolean validate() {
    NPC leprechaun = NPCs.getNearest("Tool Leprechaun");

    return plugin.getLocationQueue().isEmpty()
        && (plugin.getCurrentLocation() == null || plugin.isCurrentLocationDone())
        && SluweInventory.hasAnyItemInventoryOrEquipped(
            ItemID.SEED_DIBBER, ItemID.SPADE, ItemID.MAGIC_SECATEURS)
        && leprechaun != null;
  }

  @Override
  public void execute() {
    if (Equipment.contains(ItemID.MAGIC_SECATEURS)) {
      Equipment.getFirst(ItemID.MAGIC_SECATEURS).interact(0);
    }

    NPC leprechaun = NPCs.getNearest("Tool Leprechaun");

    leprechaun.interact("Exchange");
    Time.sleepTicksUntil(() -> Widgets.isVisible(Constants.TOOLS_WIDGET.get()), 30);

    Constants.TOOLS_DEPOSIT_SECATEURS_WIDGET.get().interact(0);
    if (Inventory.contains(ItemID.SEED_DIBBER)) {
      Constants.TOOLS_DEPOSIT_DIBBER_WIDGET.get().interact(0);
    }
    Constants.TOOLS_DEPOSIT_SPADE_WIDGET.get().interact(0);

    if (Inventory.contains(ItemID.BUCKET)) {
      Constants.TOOLS_DEPOSIT_BUCKET_WIDGET.get().interact("Store-All");
    }

    if (Inventory.contains(
        ItemID.BOTTOMLESS_COMPOST_BUCKET, ItemID.BOTTOMLESS_COMPOST_BUCKET_22997)) {
      Constants.TOOLS_DEPOSIT_BOTTOMLESS_BUCKET_WIDGET.get().interact(0);
    }

    Constants.TOOLS_CLOSE_WIDGET.get().interact("Close");

    Item cape = Inventory.getFirst(Predicates.ids(Constants.CRAFTING_CAPE_IDS));
    if (cape != null) {
      cape.interact("Teleport");
    }

    plugin.stop("Finished farm run. Stopping plugin.");
  }
}
