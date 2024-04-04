package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.items.Shop;
import net.unethicalite.api.packets.DialogPackets;

public class Buy extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Buying wines";
  }

  @Override
  public boolean validate() {
    if (config.notedItemId() != 0) {
      return false;
    }

    return !Inventory.contains(ItemID.JUG_OF_WINE) || !Inventory.isFull();
  }

  @Override
  public void execute() {
    // leave room and close door behind us
    if (config.target().getRoom().contains(Players.getLocal())) {
      config.target().getRoom().passDoor(config.target().getRoom().getOutsideLocation());
    }

    NPC barman = NPCs.getNearest(NpcID.FAISAL_THE_BARMAN);
    if (barman == null) {
      SluweMovement.walkTo(new WorldPoint(3358, 2957, 0), this::dropJugs);
      barman = NPCs.getNearest(NpcID.FAISAL_THE_BARMAN);
    }

    if (barman == null) {
      return;
    }

    dropJugs();

    barman.interact("Trade");
    if (!Time.sleepTicksUntil(Shop::isOpen, 20)) {
      return;
    }

    if (Shop.getStock(ItemID.JUG_OF_WINE) > 0) {
      final int count = Inventory.getFreeSlots();
      Shop.buyFifty(ItemID.JUG_OF_WINE);
      DialogPackets.closeInterface();
      Time.sleepTicksUntil(() -> Inventory.getFreeSlots() < count, 5);
    }

    plugin.setHop(true);
  }

  private void dropJugs() {
    int i = 0;

    if (Inventory.contains(ItemID.JUG)) {
      for (Item jug : Inventory.getAll(ItemID.JUG)) {
        jug.drop();
        if (++i >= 10) {
          i = 0;
          Time.sleepTick();
        }
      }
    }
  }
}
