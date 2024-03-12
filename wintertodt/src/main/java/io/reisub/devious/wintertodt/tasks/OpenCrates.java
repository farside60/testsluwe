package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Config;
import io.reisub.devious.wintertodt.Wintertodt;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

public class OpenCrates extends Task {
  @Inject public Wintertodt plugin;
  @Inject public Config config;

  @Override
  public String getStatus() {
    return "Opening supply crates";
  }

  @Override
  public boolean validate() {
    return !plugin.isInWintertodtRegion()
        && config.openCrates()
        && Inventory.contains(ItemID.SUPPLY_CRATE);
  }

  @Override
  public void execute() {
    Inventory.getAll(ItemID.SUPPLY_CRATE)
        .forEach(
            crate -> {
              crate.interact("Open");
              Time.sleepTick();
            });

    Inventory.getAll(
            ItemID.PYROMANCER_BOOTS,
            ItemID.PYROMANCER_GARB,
            ItemID.PYROMANCER_HOOD,
            ItemID.PYROMANCER_ROBE,
            ItemID.WARM_GLOVES)
        .forEach(
            outfit -> {
              if (!Equipment.contains(outfit.getId())) {
                outfit.interact("Wear");
                Time.sleepTick();
              }
            });

    final Item torch = Inventory.getFirst(ItemID.BRUMA_TORCH);
    if (torch != null && !Equipment.contains(ItemID.BRUMA_TORCH)) {
      torch.interact("Wield");
      Time.sleepTick();
    }
  }
}
