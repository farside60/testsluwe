package io.reisub.devious.utils.tasks;

import com.google.inject.Injector;
import io.reisub.devious.utils.Config;
import javax.inject.Inject;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.util.Text;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.items.Bank;
import net.unethicalite.api.items.Inventory;

public class KittenTask extends ParentTask {
  public static boolean handleKitten;
  private final Config config;

  @Inject
  private KittenTask(Config config) {
    this.config = config;
  }

  public static KittenTask getInstance(Injector injector) {
    KittenTask instance = injector.getInstance(KittenTask.class);

    instance.addChildren(
        injector.getInstance(HandleKitten.class), injector.getInstance(PickupCat.class));

    return instance;
  }

  public static void withdrawKittenFood(Config config) {
    if (!Bank.isOpen()) {
      return;
    }

    if (config.handleKitten()) {
      if (config.kittenFoodAmount() == 0) {
        if (Bank.contains(config.kittenFood())) {
          Bank.withdrawAll(config.kittenFood(), Bank.WithdrawMode.ITEM);
        }
      } else {
        int count = config.kittenFoodAmount() - Inventory.getCount(config.kittenFood());

        Bank.withdraw(config.kittenFood(), count, Bank.WithdrawMode.ITEM);
      }
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    final String message = Text.standardize(chatMessage.getMessage());
    final String attentionMessage1 = "your kitten wants attention.";
    final String attentionMessage2 = "your kitten really wants attention.";
    final String hungerMessage1 = "your kitten is hungry.";
    final String hungerMessage2 = "your kitten is very hungry.";

    if (config.waitUntilSecondMessage()) {
      if (message.equals(hungerMessage2) || message.equals(attentionMessage2)) {
        handleKitten = true;
      }
    } else {
      if (message.equals(hungerMessage1)
          || message.equals(hungerMessage2)
          || message.equals(attentionMessage1)
          || message.equals(attentionMessage2)) {
        handleKitten = true;
      }
    }
  }
}
