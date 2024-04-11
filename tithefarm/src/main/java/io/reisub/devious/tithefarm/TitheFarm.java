package io.reisub.devious.tithefarm;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.tithefarm.tasks.BuyRewards;
import io.reisub.devious.tithefarm.tasks.Deposit;
import io.reisub.devious.tithefarm.tasks.EnterFarm;
import io.reisub.devious.tithefarm.tasks.FillCans;
import io.reisub.devious.tithefarm.tasks.GetTools;
import io.reisub.devious.tithefarm.tasks.Harvest;
import io.reisub.devious.tithefarm.tasks.LeaveFarm;
import io.reisub.devious.tithefarm.tasks.Plant;
import io.reisub.devious.tithefarm.tasks.StartRun;
import io.reisub.devious.tithefarm.tasks.TakeSeeds;
import io.reisub.devious.tithefarm.tasks.Water;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Tithe Farm",
    description = "It ain't much, but it's honest work",
    enabledByDefault = false)
@Slf4j
public class TitheFarm extends TickScript {
  public static final int TITHE_FARM_REGION = 7222;
  public static final Set<Integer> SEED_IDS =
      ImmutableSet.of(ItemID.GOLOVANOVA_SEED, ItemID.BOLOGANO_SEED, ItemID.LOGAVANO_SEED);
  @Inject private Config config;
  @Getter @Setter private boolean startedRun;
  @Getter @Setter private boolean finishedPlanting;
  @Getter @Setter private volatile boolean gricollersFull;

  public static boolean isInTitheFarm() {
    return Static.getClient().isInInstancedRegion() && Utils.isInMapRegion(TITHE_FARM_REGION);
  }

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
    checkGricollers();

    addTask(BuyRewards.class);
    addTask(TakeSeeds.class);
    addTask(EnterFarm.class);
    addTask(GetTools.class);
    addTask(FillCans.class);
    addTask(Deposit.class);
    addTask(LeaveFarm.class);
    addTask(StartRun.class);
    addTask(Plant.class);
    addTask(Water.class);
    addTask(Harvest.class);

    startedRun = false;
    finishedPlanting = false;
  }

  private void checkGricollers() {
    final Item gricollersCan = Inventory.getFirst(ItemID.GRICOLLERS_CAN);
    if (gricollersCan == null) {
      return;
    }

    gricollersCan.interact("Check");
  }

  public boolean shouldGetBetterSeeds() {
    if (Skills.getLevel(Skill.FARMING) >= 74) {
      return !Inventory.contains(ItemID.LOGAVANO_SEED);
    }

    return Skills.getLevel(Skill.FARMING) >= 54 && !Inventory.contains(ItemID.BOLOGANO_SEED);
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (!isRunning()) {
      return;
    }

    if (chatMessage.getType() != ChatMessageType.SPAM
        && chatMessage.getType() != ChatMessageType.GAMEMESSAGE) {
      return;
    }

    if (!Inventory.contains(ItemID.GRICOLLERS_CAN)) {
      return;
    }

    if (chatMessage.getMessage().startsWith("You fill the watering can")
        || chatMessage.getMessage().startsWith("Gricoller's can is already full.")
        || chatMessage.getMessage().equals("Watering can charges remaining: 100.0%")) {
      setGricollersFull(true);
    }
  }
}
