package io.reisub.devious.tempoross;

import com.google.common.collect.ImmutableSet;
import com.google.inject.Provides;
import io.reisub.devious.tempoross.tasks.Attack;
import io.reisub.devious.tempoross.tasks.Cook;
import io.reisub.devious.tempoross.tasks.DodgeFire;
import io.reisub.devious.tempoross.tasks.DouseFire;
import io.reisub.devious.tempoross.tasks.EnterBoat;
import io.reisub.devious.tempoross.tasks.FillBuckets;
import io.reisub.devious.tempoross.tasks.Fish;
import io.reisub.devious.tempoross.tasks.HandleBank;
import io.reisub.devious.tempoross.tasks.LeaveBoat;
import io.reisub.devious.tempoross.tasks.LeaveGame;
import io.reisub.devious.tempoross.tasks.Repair;
import io.reisub.devious.tempoross.tasks.Stock;
import io.reisub.devious.tempoross.tasks.Tether;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Run;
import java.time.Duration;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.HitsplatID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.NpcID;
import net.runelite.api.ObjectID;
import net.runelite.api.Skill;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameObjectDespawned;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.util.Text;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Vars;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Tempoross",
    description = "Plays the Tempoross minigame",
    enabledByDefault = false)
@Slf4j
public class Tempoross extends TickScript {
  public static final Activity COOKING = new Activity("Cooking");
  public static final Activity DOUSING_FIRE = new Activity("Dousing fire");
  public static final Activity FILLING_BUCKETS = new Activity("Filling buckets");
  public static final Activity FISHING = new Activity("Fishing");
  public static final Activity REPAIRING = new Activity("Reparing");
  public static final Activity STOCKING_CANNON = new Activity("Stocking cannon");
  public static final Activity TETHERING_MAST = new Activity("Tethering mast");
  private static final String WAVE_INCOMING_MESSAGE = "a colossal wave closes in...";
  private static final String WAVE_FAILED_MESSAGE = "the wave slams into you";
  private static final String TETHER_MESSAGE = "you securely tether yourself";
  private static final String UNTETHER_MESSAGE = "you untether yourself";
  private static final String TEMPOROSS_VULNERABLE_MESSAGE = "tempoross is vulnerable";
  private static final String FINISHED_GAME = "the spirit anglers will ferry you back";
  private static final int VARB_IS_TETHERED = 11895;
  private static final int TEMPOROSS_REGION = 12078;
  private static final int UNKAH_REWARD_POOL_REGION = 12588;
  private static final int UNKAH_BOAT_REGION = 12332;
  @Inject private Config config;
  @Inject private TemporossOverlay overlay;
  @Getter private boolean waveIncoming;
  @Getter private int phase = 1;
  @Getter private int playersReady;
  @Getter private int energy;
  @Getter private int essence;
  @Getter private int stormIntensity;
  @Getter private int rawFish;
  @Getter private int cookedFish;
  @Getter private WorldPoint dudiPos = null;
  @Getter private boolean finished;
  @Getter @Setter private int cookedFishRequired;
  @Getter private int lastDoubleSpawn;
  @Getter @Setter private WorldPoint lastFishLocation;
  @Getter private int gamesWon;
  @Getter @Setter private int gamesLost;
  private int totalRoundTimes;
  @Getter private int permitsEarned;

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
    reset();

    tasks.add(new Run());

    addTask(HandleBank.class);
    addTask(DodgeFire.class);
    addTask(LeaveGame.class);
    addTask(EnterBoat.class);
    addTask(FillBuckets.class);
    addTask(LeaveBoat.class);
    addTask(Repair.class);
    addTask(Tether.class);
    addTask(DouseFire.class);
    addTask(Attack.class);
    addTask(Stock.class);
    addTask(Cook.class);
    addTask(Fish.class);

    trackExperience(Skill.FISHING, Skill.COOKING, Skill.CONSTRUCTION);
    gamesWon = 0;
    gamesLost = 0;
    totalRoundTimes = 0;
    permitsEarned = 0;

    setOverlay(overlay);
  }

  private void reset() {
    waveIncoming = false;
    phase = 1;
    rawFish = 0;
    cookedFish = 0;
    cookedFishRequired = 17;
    dudiPos = null;
    finished = false;
  }

  @Subscribe(priority = 1)
  private void onGameTick(GameTick event) {
    if (isInDesert() || isOnBoat()) {
      if (dudiPos != null) {
        reset();
      }

      playersReady = parseWidget(687, 3);
    } else if (isInTemporossArea()) {
      energy = parseWidget(437, 35);
      essence = parseWidget(437, 45);
      stormIntensity = parseWidget(437, 55);
    }
  }

  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (chatMessage.getType() != ChatMessageType.GAMEMESSAGE
        && chatMessage.getType() != ChatMessageType.SPAM) {
      return;
    }

    String message = Text.standardize(chatMessage.getMessage());
    if (message.contains(WAVE_INCOMING_MESSAGE)) {
      waveIncoming = true;
    } else if (message.contains(TETHER_MESSAGE)) {
      setActivity(TETHERING_MAST);
    } else if (message.contains(UNTETHER_MESSAGE) || message.contains(WAVE_FAILED_MESSAGE)) {
      waveIncoming = false;
      setActivity(Activity.IDLE);
    } else if (message.contains(TEMPOROSS_VULNERABLE_MESSAGE)) {
      phase++;
    } else if (message.contains(FINISHED_GAME)) {
      finished = true;
    } else if (message.startsWith("congratulations, you've just advanced")) {
      if (isCurrentActivity(COOKING)) {
        setActivity(Activity.IDLE);
      }
    } else if (message.startsWith("reward permits:")) {
      gamesWon++;

      Pattern regex = Pattern.compile("\\d+");
      Matcher matcher = regex.matcher(message);

      if (matcher.find()) {
        String match = matcher.group(0);

        permitsEarned += Integer.parseInt(match);
      }
    } else if (message.startsWith("subdued in")) {
      Pattern regex = Pattern.compile("\\d+:\\d+");
      Matcher matcher = regex.matcher(message);

      if (matcher.find()) {
        String match = matcher.group(0);

        String[] splitMatch = match.split(":");

        int roundTime = Integer.parseInt(splitMatch[0]) * 60 + Integer.parseInt(splitMatch[1]);

        totalRoundTimes += roundTime;
      }
    }
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    if (!Utils.isLoggedIn()) {
      return;
    }

    if (Players.getLocal() == null || event.getActor() != Players.getLocal()) {
      return;
    }

    switch (Players.getLocal().getAnimation()) {
      case AnimationID.COOKING_RANGE:
        TileObject shrine = TileObjects.getNearest(ObjectID.SHRINE_41236);
        if (shrine != null && shrine.distanceTo(Players.getLocal()) <= 3) {
          setActivity(COOKING);
        }
        break;
      case AnimationID.CONSTRUCTION:
      case AnimationID.CONSTRUCTION_IMCANDO:
        setActivity(REPAIRING);
        break;
      case AnimationID.LOOKING_INTO:
        if (!wasPreviousActivity(TETHERING_MAST)) {
          setActivity(FILLING_BUCKETS);
        }
        break;
      default:
    }
  }

  @Subscribe
  private void onInteractingChanged(InteractingChanged event) {
    if (!isRunning()) {
      return;
    }

    if (event.getSource() == Players.getLocal()) {
      if (event.getTarget() == null) {
        if (isCurrentActivity(FISHING)
            || isCurrentActivity(DOUSING_FIRE)
            || isCurrentActivity(Activity.ATTACKING)
            || isCurrentActivity(STOCKING_CANNON)) {
          setActivity(Activity.IDLE);
        }
      } else {
        String name = event.getTarget().getName();

        if (name != null) {
          if (name.equals("Fishing spot")) {
            setActivity(FISHING);
          } else if (name.contains("Fire")) {
            setActivity(DOUSING_FIRE);
          } else if (name.contains("Spirit pool")) {
            setActivity(Activity.ATTACKING);
          } else if (name.contains("Ammunition crate")) {
            setActivity(STOCKING_CANNON);
          }
        }
      }
    }
  }

  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    final ItemContainer container = event.getItemContainer();

    if (container.count(ItemID.HARPOONFISH) < cookedFish) {
      cookedFishRequired--;
    }

    rawFish = container.count(ItemID.RAW_HARPOONFISH);
    cookedFish = container.count(ItemID.HARPOONFISH);
    int emptyBuckets = container.count(ItemID.BUCKET);

    if (rawFish == 0 && isCurrentActivity(COOKING)) {
      setActivity(Activity.IDLE);
    } else if (Inventory.isFull() && isCurrentActivity(FISHING)) {
      setActivity(Activity.IDLE);
    } else if ((cookedFish == 0 || cookedFishRequired == 0) && isCurrentActivity(STOCKING_CANNON)) {
      if (cookedFishRequired == 0) {
        if (phase == 1) {
          cookedFishRequired = 19;
        } else if (phase == 2) {
          cookedFishRequired = 19;
        } else if (phase >= 3) {
          cookedFishRequired = 28;
        }
      }

      setActivity(Activity.IDLE);
    } else if (emptyBuckets == 0 && isCurrentActivity(FILLING_BUCKETS)) {
      setActivity(Activity.IDLE);
    }
  }

  @Subscribe
  private void onGameObjectDespawned(GameObjectDespawned gameObjectDespawned) {
    if (!isInTemporossArea()) {
      return;
    }

    int id = gameObjectDespawned.getGameObject().getId();

    Set<Integer> brokenMastsTotems =
        ImmutableSet.of(
            ObjectID.DAMAGED_MAST_40996,
            ObjectID.DAMAGED_MAST_40997,
            ObjectID.DAMAGED_TOTEM_POLE,
            ObjectID.DAMAGED_TOTEM_POLE_41011);

    if (brokenMastsTotems.contains(id)) {
      if (isCurrentActivity(REPAIRING)) {
        setActivity(Activity.IDLE);
      }
    }
  }

  @Subscribe
  private void onNpcSpawned(NpcSpawned npcSpawned) {
    int id = npcSpawned.getNpc().getId();

    if (id == NpcID.CAPTAIN_DUDI_10587 && dudiPos == null) {
      dudiPos = npcSpawned.getNpc().getWorldLocation();
    } else if (id == NpcID.FISHING_SPOT_10569) {
      lastDoubleSpawn = Static.getClient().getTickCount();
    }
  }

  @Subscribe
  private void onNpcDespawned(NpcDespawned npcDespawned) {
    if (!isInTemporossArea()) {
      return;
    }

    if (!isCurrentActivity(FISHING)) {
      return;
    }

    if (npcDespawned.getNpc().getId() == NpcID.FISHING_SPOT_10569
        || npcDespawned.getNpc().getId() == NpcID.FISHING_SPOT_10565) {
      if (npcDespawned.getNpc().getWorldLocation().equals(lastFishLocation)) {
        setActivity(Activity.IDLE);
      }
    }
  }

  @Subscribe
  private void onHitsplatApplied(HitsplatApplied event) {
    if (event.getActor().getName().contains("Spirit pool")
        && event.getHitsplat().getHitsplatType() == HitsplatID.DAMAGE_ME) {
      if (isCurrentActivity(Activity.ATTACKING) && phase <= 3 && essence <= 10) {
        setActivity(Activity.IDLE);
      }
    }
  }

  public boolean isOnBoat() {
    return Utils.isInRegion(UNKAH_BOAT_REGION);
  }

  public boolean isInDesert() {
    return Utils.isInRegion(UNKAH_REWARD_POOL_REGION);
  }

  public boolean isInTemporossArea() {
    return Utils.isInMapRegion(TEMPOROSS_REGION) || Utils.isInRegion(TEMPOROSS_REGION);
  }

  public WorldArea getBoatArea() {
    return new WorldArea(dudiPos.dx(-3).dy(-13), 7, 23);
  }

  public WorldArea getIslandArea() {
    return new WorldArea(dudiPos.dx(-3), 20, 30);
  }

  public String getAverageRoundTime() {
    if (gamesWon == 0) {
      return "n/a";
    }

    Duration averageTime = Duration.ofSeconds(totalRoundTimes / gamesWon);

    return String.format("%d:%02d", averageTime.toMinutes(), averageTime.toSecondsPart());
  }

  @Override
  protected void checkActionTimeout() {
    if (Vars.getBit(VARB_IS_TETHERED) > 0) {
      lastActionTick = Static.getClient().getTickCount();
      return;
    }

    super.checkActionTimeout();
  }

  private int parseWidget(int group, int id) {
    Widget widget = Widgets.get(group, id);
    if (widget == null || widget.getText().isEmpty()) {
      return 0;
    }

    Pattern regex = Pattern.compile("\\d+|None");
    Matcher matcher = regex.matcher(widget.getText());

    if (matcher.find()) {
      String match = matcher.group(0);
      if (match.equals("None")) {
        return 0;
      }

      return Integer.parseInt(match);
    }

    return 0;
  }
}
