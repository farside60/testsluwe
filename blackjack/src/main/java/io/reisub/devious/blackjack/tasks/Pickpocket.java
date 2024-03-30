package io.reisub.devious.blackjack.tasks;

import io.reisub.devious.blackjack.Blackjack;
import io.reisub.devious.blackjack.Config;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;

public class Pickpocket extends Task {
  @Inject private Blackjack plugin;
  @Inject private Config config;
  private NPC target;
  private int lastPickpocket;

  @Override
  public String getStatus() {
    return "Pickpocketing target";
  }

  @Override
  public boolean validate() {
    if (plugin.ticksSinceLastKnockout() >= 4) {
      return false;
    }

    if (!Inventory.contains(ItemID.JUG_OF_WINE)) {
      return false;
    }

    target = NPCs.getNearest(config.target().getId());

    return target != null
        && config.target().getRoom().contains(Players.getLocal());
  }

  @Override
  public void execute() {
    if (target == null) {
      return;
    }

    target.interact("Pickpocket");

    if (Static.getClient().getTickCount() - lastPickpocket <= 2) {
      Time.sleepTicks(2);
    } else {
      Time.sleepTick();
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (!plugin.isRunning()) {
      return;
    }

    if (!chatMessage.getType().equals(ChatMessageType.SPAM)) {
      return;
    }

    if (chatMessage.getMessage().equals("You pick the bandit's pocket.")) {
      lastPickpocket = Static.getClient().getTickCount();
    }
  }
}
