package io.reisub.devious.wintertodt;

import com.google.inject.Provides;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Eat;
import io.reisub.devious.utils.tasks.KittenTask;
import io.reisub.devious.wintertodt.tasks.Burn;
import io.reisub.devious.wintertodt.tasks.ChangeSide;
import io.reisub.devious.wintertodt.tasks.Chop;
import io.reisub.devious.wintertodt.tasks.DodgeProjectile;
import io.reisub.devious.wintertodt.tasks.Fix;
import io.reisub.devious.wintertodt.tasks.Fletch;
import io.reisub.devious.wintertodt.tasks.GoToBank;
import io.reisub.devious.wintertodt.tasks.GoToWintertodt;
import io.reisub.devious.wintertodt.tasks.HandleBank;
import io.reisub.devious.wintertodt.tasks.Hop;
import io.reisub.devious.wintertodt.tasks.Light;
import io.reisub.devious.wintertodt.tasks.MoveToBrazier;
import io.reisub.devious.wintertodt.tasks.OpenCrates;
import io.reisub.devious.wintertodt.tasks.OpenInventory;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.AnimationID;
import net.runelite.api.ChatMessageType;
import net.runelite.api.InventoryID;
import net.runelite.api.ItemContainer;
import net.runelite.api.ItemID;
import net.runelite.api.MessageNode;
import net.runelite.api.Projectile;
import net.runelite.api.Skill;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.ProjectileMoved;
import net.runelite.api.events.StatChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.widgets.Widget;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Skills;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Wintertodt",
    description = "The cold of the Wintertodt seeps into your bones.",
    enabledByDefault = false)
@Slf4j
public class Wintertodt extends TickScript {
  public static final int WINTERTODT_REGION = 6462;
  public static final int WINTERTODT_HEALTH_PACKED_ID = 25952276;
  public static Activity FEEDING_BRAZIER = new Activity("Feeding brazier");
  public static Activity FIXING_BRAZIER = new Activity("Fixing brazier");
  public static Activity FLETCHING = new Activity("Fletching");
  public static Activity LIGHTING_BRAZIER = new Activity("Lightning brazier");
  public static Activity WOODCUTTING = new Activity("Woodcutting");
  @Getter private final List<WintertodtProjectile> projectiles = new ArrayList<>();
  @Inject public Config config;
  @Inject public io.reisub.devious.utils.Config utilsConfig;
  @Inject private WintertodtOverlay overlay;
  @Getter private int respawnTimer;
  @Getter private int bossHealth;
  @Getter @Setter private boolean tooCold;
  @Getter @Setter private Instant lastHop;
  private Eat eatTask;
  private Scouter scouter;
  private int fmLevel;
  private int wcLevel;
  private int fletchLevel;
  @Getter private int wonGames;
  @Getter private int lostGames;
  @Getter private Instant lastWin;

  @SuppressWarnings("unused")
  @Provides
  Config provideConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    fmLevel = Skills.getLevel(Skill.FIREMAKING);
    wcLevel = Skills.getLevel(Skill.WOODCUTTING);
    fletchLevel = Skills.getLevel(Skill.FLETCHING);

    if (config.hop()) {
      scouter = injector.getInstance(Scouter.class);
    }

    eatTask = injector.getInstance(Eat.class);
    eatTask.setThreshold(config.eatThreshold());
    eatTask.setCheckMissing(config.checkMissing());

    tasks.add(eatTask);
    if (config.dodgeProjectiles()) {
      addTask(DodgeProjectile.class);
    }
    if (utilsConfig.handleKitten()) {
      addTask(KittenTask.getInstance(injector));
    }
    addTask(OpenInventory.class);
    addTask(OpenCrates.class);
    addTask(GoToBank.class);
    addTask(HandleBank.class);
    addTask(GoToWintertodt.class);
    if (config.hop()) {
      addTask(Hop.class);
    }
    addTask(MoveToBrazier.class);
    addTask(Fix.class);
    addTask(Light.class);
    addTask(Fletch.class);
    addTask(ChangeSide.class);
    addTask(Burn.class);
    addTask(Chop.class);

    reset();
    setOverlay(overlay);
  }

  @Override
  protected void onStop() {
    scouter = null;
  }

  private void reset() {
    wonGames = 0;
    lostGames = 0;

    trackExperience(Skill.FIREMAKING, Skill.WOODCUTTING, Skill.FLETCHING, Skill.CONSTRUCTION);
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onStatChanged(StatChanged event) {
    Skill skill = event.getSkill();
    int level = event.getLevel();

    if (skill == Skill.FIREMAKING && level > fmLevel) {
      setActivity(Activity.IDLE);
      fmLevel = event.getLevel();
    } else if (skill == Skill.WOODCUTTING && level > wcLevel) {
      setActivity(Activity.IDLE);
      wcLevel = event.getLevel();
    } else if (skill == Skill.FLETCHING && level > fletchLevel) {
      setActivity(Activity.IDLE);
      fletchLevel = event.getLevel();
    } else if (skill == Skill.FIREMAKING && isCurrentActivity(LIGHTING_BRAZIER)) {
      setActivity(Activity.IDLE);
    } else if (skill == Skill.CONSTRUCTION && isCurrentActivity(FIXING_BRAZIER)) {
      setActivity(Activity.IDLE);
    }
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onGameTick(GameTick event) {
    if (!isInWintertodtRegion() || !Utils.isLoggedIn()) {
      return;
    }

    if (scouter != null) {
      scouter.onGameTick();
    }

    parseBossHealth();
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onChatMessage(ChatMessage chatMessage) {
    if (!isInWintertodtRegion()) {
      return;
    }

    ChatMessageType chatMessageType = chatMessage.getType();
    if (chatMessageType != ChatMessageType.GAMEMESSAGE && chatMessageType != ChatMessageType.SPAM) {
      return;
    }

    MessageNode messageNode = chatMessage.getMessageNode();
    final InterruptType interruptType;

    if (messageNode.getValue().startsWith("You carefully fletch the root")) {
      setActivity(FLETCHING);
      return;
    }

    if (messageNode.getValue().startsWith("You have gained a supply crate")) {
      wonGames++;
      lastWin = Instant.now();
    } else if (messageNode.getValue().startsWith("You did not earn enough points")) {
      lostGames++;
    }

    if (messageNode.getValue().startsWith("The cold of")) {
      interruptType = InterruptType.COLD;
    } else if (messageNode.getValue().startsWith("The freezing cold attack")) {
      interruptType = InterruptType.SNOWFALL;
    } else if (messageNode.getValue().startsWith("The brazier is broken and shrapnel")) {
      interruptType = InterruptType.BRAZIER;
    } else if (messageNode.getValue().startsWith("You have run out of bruma roots")) {
      interruptType = InterruptType.OUT_OF_ROOTS;
    } else if (messageNode.getValue().startsWith("Your inventory is too full")) {
      interruptType = InterruptType.INVENTORY_FULL;
    } else if (messageNode.getValue().startsWith("You fix the brazier")) {
      interruptType = InterruptType.FIXED_BRAZIER;
    } else if (messageNode.getValue().startsWith("You light the brazier.")) {
      interruptType = InterruptType.LIT_BRAZIER;
    } else if (messageNode.getValue().startsWith("The brazier has gone out.")) {
      interruptType = InterruptType.BRAZIER_WENT_OUT;
    } else if (messageNode.getValue().startsWith("You eat")) {
      interruptType = InterruptType.EAT;
    } else if (messageNode.getValue().startsWith("Your hands are too cold")) {
      interruptType = InterruptType.TOO_COLD;
      tooCold = true;
    } else {
      return;
    }

    boolean wasInterrupted;

    switch (interruptType) {
      case EAT:
      case LIT_BRAZIER:
      case INVENTORY_FULL:
      case OUT_OF_ROOTS:
      case BRAZIER_WENT_OUT:
      case FIXED_BRAZIER:
        wasInterrupted = true;
        break;
      case COLD:
      case BRAZIER:
      case SNOWFALL:
        wasInterrupted = !isCurrentActivity(Activity.IDLE) && !isCurrentActivity(WOODCUTTING);
        break;
      default:
        wasInterrupted = false;
        break;
    }

    if (wasInterrupted) {
      setActivity(Activity.IDLE);
    }
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    if (!Utils.isLoggedIn() || !isInWintertodtRegion()) {
      return;
    }

    if (event.getActor() != Players.getLocal()) {
      return;
    }

    int animId = Players.getLocal().getAnimation();
    switch (animId) {
      case AnimationID.WOODCUTTING_BRONZE:
      case AnimationID.WOODCUTTING_STEEL:
      case AnimationID.WOODCUTTING_IRON:
      case AnimationID.WOODCUTTING_BLACK:
      case AnimationID.WOODCUTTING_MITHRIL:
      case AnimationID.WOODCUTTING_ADAMANT:
      case AnimationID.WOODCUTTING_RUNE:
      case AnimationID.WOODCUTTING_GILDED:
      case AnimationID.WOODCUTTING_DRAGON:
      case AnimationID.WOODCUTTING_DRAGON_OR:
      case AnimationID.WOODCUTTING_INFERNAL:
      case AnimationID.WOODCUTTING_3A_AXE:
      case AnimationID.WOODCUTTING_CRYSTAL:
      case AnimationID.WOODCUTTING_TRAILBLAZER:
        setActivity(WOODCUTTING);
        break;
      case AnimationID.FLETCHING_BOW_CUTTING:
        setActivity(FLETCHING);
        break;
      case AnimationID.LOOKING_INTO:
        setActivity(FEEDING_BRAZIER);
        break;
      case AnimationID.FIREMAKING:
        setActivity(LIGHTING_BRAZIER);
        break;
      case AnimationID.CONSTRUCTION:
      case AnimationID.CONSTRUCTION_IMCANDO:
        setActivity(FIXING_BRAZIER);
        break;
      default:
        break;
    }
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onItemContainerChanged(ItemContainerChanged event) {
    final ItemContainer container = event.getItemContainer();

    if (!isInWintertodtRegion()
        || container != Static.getClient().getItemContainer(InventoryID.INVENTORY)) {
      return;
    }

    int logs = Inventory.getCount(ItemID.BRUMA_ROOT);
    int kindling = Inventory.getCount(ItemID.BRUMA_KINDLING);

    if (logs == 0 && isCurrentActivity(FLETCHING)) {
      setActivity(Activity.IDLE);
    } else if (logs == 0 && kindling == 0 && isCurrentActivity(FEEDING_BRAZIER)) {
      setActivity(Activity.IDLE);
    } else if (shouldStartFeeding()
        && (isCurrentActivity(WOODCUTTING) || isCurrentActivity(FLETCHING))) {
      setActivity(Activity.IDLE);
    } else if (shouldStartFletching() && isCurrentActivity(WOODCUTTING)) {
      setActivity(Activity.IDLE);
    }
  }

  @SuppressWarnings("unused")
  @Subscribe
  private void onVarbitChanged(VarbitChanged varbitChanged) {
    if (varbitChanged.getVarbitId() == Varbits.WINTERTODT_TIMER) {
      respawnTimer = varbitChanged.getValue() * 30 / 50;
    }
  }

  @Subscribe
  private void onProjectileMoved(ProjectileMoved event) {
    Projectile projectile = event.getProjectile();

    if (projectile.getInteracting() != null) {
      return;
    }

    if (projectile.getId() == 501) {
      int x = Static.getClient().getBaseX() + event.getPosition().getSceneX();
      int y = Static.getClient().getBaseY() + event.getPosition().getSceneY();
      int cycles = projectile.getEndCycle() - projectile.getStartCycle();

      // we don't care about any of the projectiles that don't go to our play area
      if (y >= 4001) {
        return;
      }

      if (cycles == 200) {
        projectiles.add(new WintertodtProjectile(x, y, true, Instant.now()));
      } else if ((x == 1638 && y == 3997) || (x == 1620 && y == 3997)) {
        if (cycles == 120) {
          projectiles.add(new WintertodtProjectile(x, y, false, Instant.now()));
        }
      }
    }
  }

  @Subscribe
  private void onConfigChanged(ConfigChanged event) {
    if (!event.getGroup().equals("sluwewintertodt")) {
      return;
    }

    if (event.getKey().equals("eatThreshold")) {
      eatTask.setThreshold(config.eatThreshold());
    } else if (event.getKey().equals("checkMissing")) {
      eatTask.setCheckMissing(config.checkMissing());
    }
  }

  public boolean isInWintertodtRegion() {
    return Utils.isInRegion(WINTERTODT_REGION);
  }

  public boolean bossIsUp() {
    return isInWintertodtRegion() && getBossHealth() > 0 && getRespawnTimer() <= 0;
  }

  public boolean shouldStartFletching() {
    int rootCount = Inventory.getCount(ItemID.BRUMA_ROOT);
    int kindlingCount = Inventory.getCount(ItemID.BRUMA_KINDLING);

    return rootCount * 2 + kindlingCount >= getBossHealth();
  }

  public boolean shouldStartFeeding() {
    int burnablesCount = Inventory.getCount(Predicates.nameContains("Bruma"));

    return burnablesCount >= getBossHealth();
  }

  public Side getNearestSide() {
    WorldPoint playerPoint = Players.getLocal().getWorldLocation();
    if (playerPoint.distanceTo(Side.EAST.getPositionNearBrazier())
        > playerPoint.distanceTo(Side.WEST.getPositionNearBrazier())) {
      return Side.WEST;
    } else {
      return Side.EAST;
    }
  }

  public Side getFurthestSide() {
    WorldPoint playerPoint = Players.getLocal().getWorldLocation();
    if (playerPoint.distanceTo(Side.EAST.getPositionNearBrazier())
        < playerPoint.distanceTo(Side.WEST.getPositionNearBrazier())) {
      return Side.WEST;
    } else {
      return Side.EAST;
    }
  }

  private void parseBossHealth() {
    Widget healthWidget = Widgets.fromId(WINTERTODT_HEALTH_PACKED_ID);

    if (healthWidget != null) {
      Pattern regex = Pattern.compile("\\d+");
      Matcher bossHealthMatcher = regex.matcher(healthWidget.getText());

      if (bossHealthMatcher.find()) {
        bossHealth = Integer.parseInt(bossHealthMatcher.group(0));
        if (bossHealth > 0) {
          respawnTimer = -1;
        }
      } else {
        bossHealth = -1;
      }
    }
  }
}
