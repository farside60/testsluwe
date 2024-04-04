package io.reisub.devious.mining.tasks;

import io.reisub.devious.mining.Config;
import io.reisub.devious.mining.Location;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.client.Static;

public class CastSuperheat extends Task {
  @Inject private Config config;
  @Inject private Mine mineTask;
  private int lastTick;

  @Override
  public String getStatus() {
    return "Casting Superheat";
  }

  @Override
  public boolean validate() {
    if (config.location() != Location.MONASTERY_IRON) {
      return false;
    }

    return config.superheat()
        && Static.getClient().getTickCount() - lastTick > 3
        && SpellBook.Standard.SUPERHEAT_ITEM.canCast()
        && Inventory.contains(ItemID.IRON_ORE);
  }

  @Override
  public void execute() {
    mineTask.setCurrentRockPosition(null);

    Item ironOre = Inventory.getFirst(ItemID.IRON_ORE);
    if (ironOre == null) {
      return;
    }

    Magic.cast(SpellBook.Standard.SUPERHEAT_ITEM, ironOre);
    lastTick = Static.getClient().getTickCount();
  }
}
