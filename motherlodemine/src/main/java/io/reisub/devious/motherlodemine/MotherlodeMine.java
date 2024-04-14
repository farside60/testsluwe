package io.reisub.devious.motherlodemine;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.motherlodemine.tasks.Deposit;
import io.reisub.devious.motherlodemine.tasks.FixWheel;
import io.reisub.devious.motherlodemine.tasks.GoDown;
import io.reisub.devious.motherlodemine.tasks.GoToMiningArea;
import io.reisub.devious.motherlodemine.tasks.GoUp;
import io.reisub.devious.motherlodemine.tasks.HandleBank;
import io.reisub.devious.motherlodemine.tasks.Mine;
import io.reisub.devious.motherlodemine.tasks.UseShortcut;
import io.reisub.devious.motherlodemine.tasks.WithdrawSack;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import java.util.Set;
import javax.inject.Inject;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Actor;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.Perspective;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.Varbits;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Motherlode Mine",
    description = "Diggy, diggy hole",
    enabledByDefault = false)
@Slf4j
public class MotherlodeMine extends TickScript {

  public static final Activity MINING = new Activity("Mining");
  public static final Activity REPAIRING = new Activity("Repairing");
  public static final Activity ENTERING_TUNNEL = new Activity("Entering tunnel");
  private static final Set<Integer> MOTHERLODE_MAP_REGIONS =
      ImmutableSet.of(14679, 14680, 14681, 14935, 14936, 14937, 15191, 15192, 15193);
  private static final int SACK_LARGE_SIZE = 162;
  private static final int SACK_SIZE = 81;
  private static final int UPPER_FLOOR_HEIGHT = -490;

  @Inject private Config config;
  @Inject private MotherlodeMineOverlay overlay;
  private int curSackSize;
  private int maxSackSize;
  @Getter private boolean sackFull;
  private int lastStatChangedHashcode;
  @Getter private int strutsFixed;
  @Getter private int nuggets;
  @Getter private int payDirt;
  @Getter private int coal;
  @Getter private int goldOre;
  @Getter private int mithrilOre;
  @Getter private int adamantiteOre;
  @Getter private int runiteOre;
  @Getter private int sapphires;
  @Getter private int emeralds;
  @Getter private int rubies;
  @Getter private int diamonds;

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
    addTask(WithdrawSack.class);
    addTask(UseShortcut.class);
    addTask(Mine.class);
    addTask(GoDown.class);
    addTask(FixWheel.class);
    addTask(Deposit.class);
    addTask(HandleBank.class);
    addTask(GoToMiningArea.class);
    addTask(GoUp.class);

    trackExperience(Skill.MINING);
    reset();

    setOverlay(overlay);
  }

  private void reset() {
    strutsFixed = 0;
    nuggets = 0;
    payDirt = 0;
    coal = 0;
    goldOre = 0;
    mithrilOre = 0;
    adamantiteOre = 0;
    runiteOre = 0;
    sapphires = 0;
    emeralds = 0;
    rubies = 0;
    diamonds = 0;
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    Actor actor = event.getActor();
    if (!isRunning() || actor == null || actor != Players.getLocal()) {
      return;
    }

    switch (Players.getLocal().getAnimation()) {
      case AnimationID.MINING_MOTHERLODE_BRONZE:
      case AnimationID.MINING_MOTHERLODE_IRON:
      case AnimationID.MINING_MOTHERLODE_STEEL:
      case AnimationID.MINING_MOTHERLODE_BLACK:
      case AnimationID.MINING_MOTHERLODE_MITHRIL:
      case AnimationID.MINING_MOTHERLODE_ADAMANT:
      case AnimationID.MINING_MOTHERLODE_RUNE:
      case AnimationID.MINING_MOTHERLODE_DRAGON:
      case AnimationID.MINING_MOTHERLODE_DRAGON_OR:
      case AnimationID.MINING_MOTHERLODE_DRAGON_UPGRADED:
      case AnimationID.MINING_MOTHERLODE_CRYSTAL:
      case AnimationID.MINING_MOTHERLODE_GILDED:
      case AnimationID.MINING_MOTHERLODE_INFERNAL:
      case AnimationID.MINING_MOTHERLODE_3A:
        setActivity(MINING);
        break;
      default:
    }
  }

  @Subscribe
  private void onGameObjectDespawned(GameObjectDespawned event) {
    if (isCurrentActivity(REPAIRING) && event.getGameObject().getName().equals("Broken strut")) {
      setActivity(Activity.IDLE);
    }
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    if (isCurrentActivity(Activity.DEPOSITING)) {
      if (!Inventory.contains(ItemID.PAYDIRT)) {
        setActivity(Activity.IDLE);
      }
    } else if (isCurrentActivity(Activity.WITHDRAWING)) {
      if (Inventory.contains(
          ItemID.RUNITE_ORE,
          ItemID.ADAMANTITE_ORE,
          ItemID.MITHRIL_ORE,
          ItemID.GOLD_ORE,
          ItemID.COAL,
          ItemID.UNCUT_SAPPHIRE,
          ItemID.UNCUT_EMERALD,
          ItemID.UNCUT_RUBY,
          ItemID.UNCUT_DIAMOND,
          ItemID.UNCUT_DRAGONSTONE)) {
        setActivity(Activity.IDLE);

        coal += Inventory.getCount(ItemID.COAL);
        goldOre += Inventory.getCount(ItemID.GOLD_ORE);
        mithrilOre += Inventory.getCount(ItemID.MITHRIL_ORE);
        adamantiteOre += Inventory.getCount(ItemID.ADAMANTITE_ORE);
        runiteOre += Inventory.getCount(ItemID.RUNITE_ORE);
        nuggets += Inventory.getCount(true, ItemID.GOLDEN_NUGGET);
      }
    } else if (isCurrentActivity(MINING)) {
      if (Inventory.isFull()) {
        setActivity(Activity.IDLE);
      }
    }
  }

  @Subscribe
  private void onVarbitChanged(VarbitChanged event) {
    if (isRunning() && inMlm()) {
      refreshSackValues();
      int inventorySize = config.depositNuggets() ? 27 : 26;
      if (curSackSize >= maxSackSize - inventorySize) {
        sackFull = true;
      }
    }
  }

  @Subscribe
  private void onStatChanged(StatChanged statChanged) {
    if (!isRunning() || lastStatChangedHashcode == statChanged.hashCode()) {
      return;
    }

    lastStatChangedHashcode = statChanged.hashCode();

    if (statChanged.getSkill() == Skill.SMITHING) {
      strutsFixed++;
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (!isRunning()) {
      return;
    }

    if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE
        && chatMessage.getType() != ChatMessageType.SPAM) {
      return;
    }

    final String message = chatMessage.getMessage();

    if (message.equals("You manage to mine some pay-dirt.")) {
      payDirt++;
    } else if (message.startsWith("You just found")) {
      if (message.contains("Sapphire")) {
        sapphires++;
      } else if (message.contains("Emerald")) {
        emeralds++;
      } else if (message.contains("Ruby")) {
        rubies++;
      } else if (message.contains("Diamond")) {
        diamonds++;
      }
    }
  }

  public boolean isUpstairs() {
    return Perspective.getTileHeight(Static.getClient(), Players.getLocal().getLocalLocation(), 0)
        < UPPER_FLOOR_HEIGHT;
  }

  public void mineRockfall(final int x, final int y) {
    final TileObject rockfall =
        TileObjects.getFirstAt(
            x, y, 0, ObjectID.ROCKFALL, ObjectID.ROCKFALL_26680, ObjectID.ROCKFALL_28786);

    if (rockfall != null) {
      GameThread.invoke(() -> rockfall.interact("Mine"));
      Time.sleepTicksUntil(
          () ->
              TileObjects.getFirstAt(
                      x, y, 0, ObjectID.ROCKFALL, ObjectID.ROCKFALL_26680, ObjectID.ROCKFALL_28786)
                  == null,
          50);
    }
  }

  public MiningArea getMiningArea() {
    if (config.upstairs()) {
      return MiningArea.UPSTAIRS;
    } else if (config.shortcut()) {
      return MiningArea.BEHIND_SHORTCUT;
    } else {
      return MiningArea.NORTH;
    }
  }

  private boolean inMlm() {
    return Utils.isInMapRegion(MOTHERLODE_MAP_REGIONS);
  }

  private void refreshSackValues() {
    curSackSize = Vars.getBit(Varbits.SACK_NUMBER);
    boolean sackUpgraded = Vars.getBit(Varbits.SACK_UPGRADED) == 1;
    maxSackSize = sackUpgraded ? SACK_LARGE_SIZE : SACK_SIZE;

    if (curSackSize == 0) {
      sackFull = false;
    }
  }
}
