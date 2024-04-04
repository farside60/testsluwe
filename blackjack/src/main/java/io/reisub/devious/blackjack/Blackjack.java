package io.reisub.devious.blackjack;

import com.google.inject.Provides;
import io.reisub.devious.blackjack.tasks.Buy;
import io.reisub.devious.blackjack.tasks.CloseDoor;
import io.reisub.devious.blackjack.tasks.Eat;
import io.reisub.devious.blackjack.tasks.GoToRoom;
import io.reisub.devious.blackjack.tasks.Hop;
import io.reisub.devious.blackjack.tasks.Knockout;
import io.reisub.devious.blackjack.tasks.LureIn;
import io.reisub.devious.blackjack.tasks.LureOut;
import io.reisub.devious.blackjack.tasks.Pickpocket;
import io.reisub.devious.blackjack.tasks.RunAway;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.tasks.OpenInventory;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NPC;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Blackjack",
    description = "Pickpocketing, but with violence!",
    enabledByDefault = false)
@Slf4j
public class Blackjack extends TickScript {
  private static final String SUCCESS_BLACKJACK =
      "You smack the bandit over the head and render them unconscious.";
  private static final String FAILED_BLACKJACK = "Your blow only glances off the bandit's head.";

  @Inject private Config config;
  @Inject private BlackjackOverlay overlay;
  @Getter @Setter private boolean hop;
  @Getter @Setter private int originalWorld;
  private int lastKnockoutTick;
  @Getter private int successfulKnockOuts;
  @Getter private int failedKnockOuts;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    super.onStart();

    addTask(RunAway.class);
    addTask(Pickpocket.class);
    addTask(Eat.class);
    addTask(Hop.class);
    addTask(Buy.class);
    addTask(OpenInventory.class);
    addTask(LureOut.class);
    addTask(LureIn.class);
    addTask(GoToRoom.class);
    addTask(CloseDoor.class);
    addTask(Knockout.class);

    trackExperience(Skill.THIEVING);
    setOverlay(overlay);
  }

  @Subscribe(priority = 1000)
  private void onChatMessage(ChatMessage chatMessage) {
    if (!isRunning()) {
      return;
    }

    if (chatMessage.getType() != ChatMessageType.SPAM) {
      return;
    }

    final String message = chatMessage.getMessage();

    if (message.equals(SUCCESS_BLACKJACK) || message.equals(FAILED_BLACKJACK)) {
      if (message.equals(SUCCESS_BLACKJACK)) {
        successfulKnockOuts++;
      } else {
        failedKnockOuts++;
      }

      lastKnockoutTick = Static.getClient().getTickCount();
    }
  }

  public int ticksSinceLastKnockout() {
    return Static.getClient().getTickCount() - lastKnockoutTick;
  }

  public boolean lure(NPC target) {
    target.interact("Lure");
    if (!Time.sleepTicksUntil(Dialog::isOpen, 15)) {
      return false;
    }

    Time.sleepTicksUntil(() -> !Dialog.isOpen(), 10);
    Time.sleepTick();

    return target.getInteracting().equals(Players.getLocal());
  }

  public double getKnockoutRate() {
    if (successfulKnockOuts == 0) {
      return 0;
    }

    if (failedKnockOuts == 0) {
      return 100;
    }

    return (double) (successfulKnockOuts * 100) / (successfulKnockOuts + failedKnockOuts);
  }
}
