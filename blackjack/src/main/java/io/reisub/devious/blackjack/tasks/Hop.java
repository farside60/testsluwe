package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Worlds;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.items.Shop;
import net.unethicalite.api.packets.DialogPackets;
import net.unethicalite.client.Static;

public class Hop extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;
  private int last;

  @Override
  public String getStatus() {
    return "Hopping worlds";
  }

  @Override
  public boolean validate() {
    if (last + 3 > Static.getClient().getTickCount()) {
      return false;
    }

    // if we're not in the room and there's already a player inside we should hop
    if (Inventory.contains(ItemID.JUG_OF_WINE)
        && !config.target().getRoom().getArea().contains(Players.getLocal())
        && isPlayerInRoom()) {
      return true;
    }

    return plugin.isHop();
  }

  @Override
  public void execute() {
    if (Shop.isOpen()) {
      DialogPackets.closeInterface();
      if (!Time.sleepTicksUntil(() -> !Shop.isOpen(), 5)) {
        return;
      }
    }

    if (Inventory.isFull()
        && plugin.getOriginalWorld() != 0) {
      Worlds.hopTo(Worlds.getFirst(plugin.getOriginalWorld()));
    } else {
      Worlds.hopTo(
          Worlds.getRandom(
              w -> w.getId() != Worlds.getCurrentId() && w.isMembers() && w.isNormal()));
    }
    Time.sleepTicks(2);

    last = Static.getClient().getTickCount();
    plugin.setHop(false);

    Time.sleepTicksUntil(Utils::isLoggedIn, 20);
  }

  private boolean isPlayerInRoom() {
    return !Players.getAll(p -> config.target().getRoom().getArea().contains(p)).isEmpty();
  }
}
