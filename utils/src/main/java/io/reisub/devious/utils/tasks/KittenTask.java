package io.reisub.devious.utils.tasks;

import com.google.inject.Injector;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.eventbus.Subscribe;

public class KittenTask extends ParentTask {
  public static boolean handleKitten;

  public static KittenTask getInstance(Injector injector) {
    KittenTask instance = injector.getInstance(KittenTask.class);

    instance.addChildren(
        injector.getInstance(HandleKitten.class), injector.getInstance(PickupCat.class));

    return instance;
  }

  @Subscribe
  private void onChatMessage(ChatMessage event) {
    if (event.getMessage().contains("Your kitten wants attention")
        || event.getMessage().contains("Your kitten is hungry.")) {
      handleKitten = true;
    }
  }
}
