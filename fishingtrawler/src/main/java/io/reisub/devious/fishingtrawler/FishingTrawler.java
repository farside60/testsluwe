package io.reisub.devious.fishingtrawler;

import com.google.inject.Provides;
import io.reisub.devious.fishingtrawler.tasks.Chop;
import io.reisub.devious.fishingtrawler.tasks.EnterBoat;
import io.reisub.devious.fishingtrawler.tasks.Fix;
import io.reisub.devious.fishingtrawler.tasks.GetRewards;
import io.reisub.devious.fishingtrawler.tasks.GoToTentacle;
import io.reisub.devious.fishingtrawler.tasks.GoUp;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.NpcID;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Fishing Trawler",
    description = "Trawl your way to the Angler's outfit",
    enabledByDefault = false)
@Slf4j
public class FishingTrawler extends TickScript {
  @Inject private Config config;

  public static Activity FIXING_RAIL = new Activity("Fixing rail");
  public static Activity WOODCUTTING = new Activity("Woodcutting");
  public static final int BOAT_REGION = 7499;
  public static final int BOAT_REGION2 = 8011;
  public static final int PORT_REGION = 10545;

  @Getter private int lastConstructionTick;
  @Getter @Setter private int railAttempts;
  @Getter private boolean enoughForReward;
  @Getter @Setter private boolean takenReward = true;

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

    addTask(GoToTentacle.class);
    addTask(Fix.class);
    addTask(Chop.class);
    addTask(GetRewards.class);
    addTask(EnterBoat.class);
    addTask(GoUp.class);
  }

  @Subscribe
  private void onInteractingChanged(InteractingChanged event) {
    if (!isRunning()) {
      return;
    }

    if (event.getSource() != Players.getLocal()) {
      return;
    }

    if (event.getTarget() == null) {
      setActivity(Activity.IDLE);
    } else if (event.getTarget().getId() == NpcID.ENORMOUS_TENTACLE_10709) {
      setActivity(WOODCUTTING);
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage event) {
    if (!isRunning()) {
      return;
    }

    final ChatMessageType chatMessageType = event.getType();

    if (chatMessageType != ChatMessageType.SPAM && chatMessageType != ChatMessageType.GAMEMESSAGE) {
      return;
    }

    final String message = event.getMessage();

    if (message.equals("You have helped enough to earn a portion of the catch.")) {
      enoughForReward = true;
    } else if (message.startsWith("You have contributed enough to earn rewards!")) {
      takenReward = false;
    } else if (message.equals("You manage to fix the rail.")
        || message.equals("You fail to repair the rail in the harsh conditions.")) {
      setActivity(Activity.IDLE);
      railAttempts++;
      lastConstructionTick = Static.getClient().getTickCount();
    }
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    if (!isRunning()) {
      return;
    }

    if (event.getActor() != Players.getLocal()) {
      return;
    }

    final int constructionAnimation = 3683;

    if (Players.getLocal().getAnimation() == constructionAnimation) {
      setActivity(FIXING_RAIL);
    }
  }

  @Subscribe
  private void onNpcDespawned(NpcDespawned event) {
    if (!isRunning()) {
      return;
    }

    final int id = event.getNpc().getId();

    if (id == NpcID.ENORMOUS_TENTACLE
        || id == NpcID.ENORMOUS_TENTACLE_10708
        || id == NpcID.ENORMOUS_TENTACLE_10709) {
      if (isCurrentActivity(WOODCUTTING)) {
        setActivity(Activity.IDLE);
      }
    }
  }
}
