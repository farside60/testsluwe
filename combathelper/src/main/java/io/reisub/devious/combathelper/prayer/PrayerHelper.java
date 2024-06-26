package io.reisub.devious.combathelper.prayer;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Sets;
import io.reisub.devious.combathelper.Helper;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.enums.SluwePrayer;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import javax.inject.Singleton;
import net.runelite.api.Actor;
import net.runelite.api.AnimationID;
import net.runelite.api.GameState;
import net.runelite.api.HeadIcon;
import net.runelite.api.HitsplatID;
import net.runelite.api.NPC;
import net.runelite.api.NpcID;
import net.runelite.api.Player;
import net.runelite.api.Prayer;
import net.runelite.api.Projectile;
import net.runelite.api.ProjectileID;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.AnimationChanged;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.InteractingChanged;
import net.runelite.api.events.NpcDespawned;
import net.runelite.api.events.NpcSpawned;
import net.runelite.api.events.PlayerDespawned;
import net.runelite.api.events.PlayerSpawned;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.packets.WidgetPackets;
import net.unethicalite.api.widgets.Prayers;
import net.unethicalite.api.widgets.Tab;
import net.unethicalite.api.widgets.Tabs;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

@Singleton
public class PrayerHelper extends Helper {

  private static final Set<Integer> DEMONIC_PROJECTILES =
      ImmutableSet.of(
          ProjectileID.DEMONIC_GORILLA_RANGED,
          ProjectileID.DEMONIC_GORILLA_MAGIC,
          ProjectileID.DEMONIC_GORILLA_BOULDER);
  private static final int JALTOK_JAD_MAGE_ATTACK = 7592;
  private static final int JALTOK_JAD_RANGE_ATTACK = 7593;
  private boolean toggleFlicking;
  private boolean firstFlick;
  private boolean toggledOff;
  private boolean switchToInventory;
  private SluwePrayer currentOverhead;
  private SluwePrayer currentDefensive;
  private Set<SluwePrayer> swapPrayers;
  private int outOfSyncTicks;

  private Map<NPC, DemonicGorilla> gorillas;
  private List<WorldPoint> recentBoulders;
  private List<PendingGorillaAttack> pendingAttacks;
  private Map<Player, MemorizedPlayer> memorizedPlayers;
  private DemonicGorilla currentGorilla;

  private static boolean isNpcGorilla(int npcId) {
    return npcId == NpcID.DEMONIC_GORILLA
        || npcId == NpcID.DEMONIC_GORILLA_7145
        || npcId == NpcID.DEMONIC_GORILLA_7146
        || npcId == NpcID.DEMONIC_GORILLA_7147
        || npcId == NpcID.DEMONIC_GORILLA_7148
        || npcId == NpcID.DEMONIC_GORILLA_7149;
  }

  @Override
  public void startUp() {
    gorillas = new HashMap<>();
    recentBoulders = new ArrayList<>();
    pendingAttacks = new ArrayList<>();
    memorizedPlayers = new HashMap<>();
    GameThread.invoke(this::reset); // Updates the list of gorillas and players
  }

  @Override
  public void shutDown() {
    gorillas = null;
    recentBoulders = null;
    pendingAttacks = null;
    memorizedPlayers = null;
  }

  @Subscribe(priority = 90)
  private void onGameTick(GameTick event) {
    checkGorillaAttacks();
    checkPendingAttacks();
    updatePlayers();
    recentBoulders.clear();

    if (config.demonicGorillaFlick() && currentGorilla == null) {
      currentGorilla = getCurrentGorilla();
    }

    if (config.demonicGorillaFlick() && currentGorilla != null) {
      List<DemonicGorilla.AttackStyle> styles = currentGorilla.getNextPosibleAttackStyles();
      DemonicGorilla.AttackStyle style;

      if (styles.size() == 0) {
        style = null;
      } else if (styles.size() == 1) {
        style = styles.get(0);
      } else {
        if (styles.contains(DemonicGorilla.AttackStyle.MAGIC)) {
          style = DemonicGorilla.AttackStyle.MAGIC;
        } else if (styles.contains(DemonicGorilla.AttackStyle.RANGED)) {
          style = DemonicGorilla.AttackStyle.RANGED;
        } else {
          style = DemonicGorilla.AttackStyle.MELEE;
        }
      }

      if (style != null) {
        switch (style) {
          case MELEE:
            if (currentOverhead != SluwePrayer.PROTECT_FROM_MELEE) {
              setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
            }
            break;
          case RANGED:
            if (currentOverhead != SluwePrayer.PROTECT_FROM_MISSILES) {
              setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
            }
            break;
          case MAGIC:
            if (currentOverhead != SluwePrayer.PROTECT_FROM_MAGIC) {
              setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
            }
            break;
          default:
        }
      }
    }

    if (switchToInventory && !Tabs.isOpen(Tab.INVENTORY)) {
      Tabs.open(Tab.INVENTORY);
      switchToInventory = false;
    }

    final Widget quickPrayersWidget = Widgets.get(WidgetInfo.MINIMAP_QUICK_PRAYER_ORB);

    if (outOfSyncTicks >= 2) {
      currentOverhead = null;
      outOfSyncTicks = 0;
    }

    if (isFlicking()) {
      if (currentOverhead == null) {
        if (Prayers.isEnabled(Prayer.PROTECT_FROM_MAGIC)) {
          currentOverhead = SluwePrayer.PROTECT_FROM_MAGIC;
        } else if (Prayers.isEnabled(Prayer.PROTECT_FROM_MISSILES)) {
          currentOverhead = SluwePrayer.PROTECT_FROM_MISSILES;
        } else if (Prayers.isEnabled(Prayer.PROTECT_FROM_MELEE)) {
          currentOverhead = SluwePrayer.PROTECT_FROM_MELEE;
        }
      } else {
        if (!Prayers.isEnabled(currentOverhead.getPrayer())) {
          outOfSyncTicks++;
        } else {
          outOfSyncTicks = 0;
        }
      }
    }

    if (swapPrayers != null && !swapPrayers.isEmpty()) {
      quickPrayersWidget.interact("Setup");

      for (SluwePrayer quickPrayer : swapPrayers) {
        WidgetPackets.queueWidgetAction1Packet(5046276, -1, quickPrayer.getQuickPrayerId());
      }

      WidgetPackets.queueWidgetAction1Packet(5046277, -1, -1);
      switchToInventory = config.openInventory();

      swapPrayers = null;
    }

    if (toggleFlicking) {
      if (!Prayers.isQuickPrayerEnabled() && !firstFlick) {
        togglePrayer(quickPrayersWidget);
        return;
      }

      togglePrayer(quickPrayersWidget);
      togglePrayer(quickPrayersWidget);

      if (firstFlick) {
        firstFlick = false;
      }
    } else if (toggledOff) {
      toggledOff = false;

      if (config.deactivateAfterStopping()) {
        togglePrayer(quickPrayersWidget);
      }
    }
  }

  @Subscribe
  private void onAnimationChanged(AnimationChanged event) {
    Actor actor = event.getActor();
    if (actor == null) {
      return;
    }

    if (config.jadPrayerFlick()) {
      switch (actor.getAnimation()) {
        case AnimationID.TZTOK_JAD_MAGIC_ATTACK:
        case JALTOK_JAD_MAGE_ATTACK:
          setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
          break;
        case AnimationID.TZTOK_JAD_RANGE_ATTACK:
        case JALTOK_JAD_RANGE_ATTACK:
          setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
          break;
        default:
      }
    }
  }

  public void keyPressed(KeyEvent e) {
    if (config.prayerFlickHotkey().matches(e)) {
      toggleFlicking();
      e.consume();
    } else if (config.hotkeyMelee().matches(e)) {
      setPrayer(SluwePrayer.PROTECT_FROM_MELEE);
      e.consume();
    } else if (config.hotkeyMissiles().matches(e)) {
      setPrayer(SluwePrayer.PROTECT_FROM_MISSILES);
      e.consume();
    } else if (config.hotkeyMagic().matches(e)) {
      setPrayer(SluwePrayer.PROTECT_FROM_MAGIC);
      e.consume();
    } else if (config.hotkeyMeleeBuff().matches(e)) {
      setPrayers(SluwePrayer.getBestMeleeBuff());
      e.consume();
    } else if (config.hotkeyRangedBuff().matches(e)) {
      setPrayers(SluwePrayer.getBestRangedBuff());
      e.consume();
    } else if (config.hotkeyMagicBuff().matches(e)) {
      setPrayers(SluwePrayer.getBestMagicBuff());
      e.consume();
    }
  }

  @Subscribe
  private void onInteractingChanged(InteractingChanged event) {
    if (!Utils.isLoggedIn()) {
      return;
    }

    if (event.getSource() == null || event.getTarget() == null) {
      return;
    }

    if (event.getSource().equals(Players.getLocal()) && isNpcGorilla(event.getTarget().getId())) {
      currentGorilla = getCurrentGorilla();
    }
  }

  private void togglePrayer(Widget widget) {
    widget.interact(0);
  }

  public void toggleFlicking() {
    if (toggleFlicking) {
      toggledOff = true;
    } else {
      firstFlick = true;
    }

    toggleFlicking = !toggleFlicking;
  }

  public boolean isFlicking() {
    return toggleFlicking;
  }

  public void setPrayer(SluwePrayer quickPrayer) {
    setPrayers(ImmutableSet.of(quickPrayer), config.allowToggleOff());
  }

  public void setPrayer(SluwePrayer quickPrayer, boolean allowToggleOff) {
    setPrayers(ImmutableSet.of(quickPrayer), allowToggleOff);
  }

  public void setPrayers(Set<SluwePrayer> quickPrayers) {
    setPrayers(quickPrayers, config.allowToggleOff());
  }

  public void setPrayers(Set<SluwePrayer> quickPrayers, boolean allowToggleOff) {
    if (quickPrayers == null
        || quickPrayers.isEmpty()
        || (!allowToggleOff && quickPrayers.contains(currentOverhead))) {
      return;
    }

    quickPrayers = Sets.newHashSet(quickPrayers);

    if (quickPrayers.contains(currentOverhead)) {
      currentOverhead = null;
    } else if (quickPrayers.contains(SluwePrayer.PROTECT_FROM_MAGIC)) {
      currentOverhead = SluwePrayer.PROTECT_FROM_MAGIC;
    } else if (quickPrayers.contains(SluwePrayer.PROTECT_FROM_MISSILES)) {
      currentOverhead = SluwePrayer.PROTECT_FROM_MISSILES;
    } else if (quickPrayers.contains(SluwePrayer.PROTECT_FROM_MELEE)) {
      currentOverhead = SluwePrayer.PROTECT_FROM_MELEE;
    }

    quickPrayers.remove(currentDefensive);

    if (quickPrayers.contains(SluwePrayer.THICK_SKIN)) {
      currentDefensive = SluwePrayer.THICK_SKIN;
    } else if (quickPrayers.contains(SluwePrayer.ROCK_SKIN)) {
      currentDefensive = SluwePrayer.ROCK_SKIN;
    } else if (quickPrayers.contains(SluwePrayer.STEEL_SKIN)) {
      currentDefensive = SluwePrayer.STEEL_SKIN;
    } else if (quickPrayers.contains(SluwePrayer.CHIVALRY)) {
      currentDefensive = SluwePrayer.CHIVALRY;
    } else if (quickPrayers.contains(SluwePrayer.PIETY)) {
      currentDefensive = SluwePrayer.PIETY;
    } else if (quickPrayers.contains(SluwePrayer.RIGOUR)) {
      currentDefensive = SluwePrayer.RIGOUR;
    } else if (quickPrayers.contains(SluwePrayer.AUGURY)) {
      currentDefensive = SluwePrayer.AUGURY;
    }

    if (swapPrayers == null) {
      swapPrayers = quickPrayers;
    } else {
      swapPrayers.addAll(quickPrayers);
    }
  }

  // Gorilla code below, taken from DemonicGorilla plugin

  private DemonicGorilla getCurrentGorilla() {
    for (Map.Entry<NPC, DemonicGorilla> gorilla : gorillas.entrySet()) {
      Actor interacting = Players.getLocal().getInteracting();
      if (interacting != null && interacting.equals(gorilla.getKey())) {
        return gorilla.getValue();
      }
    }

    return null;
  }

  private void clear() {
    recentBoulders.clear();
    pendingAttacks.clear();
    memorizedPlayers.clear();
    gorillas.clear();
  }

  private void reset() {
    recentBoulders.clear();
    pendingAttacks.clear();
    resetGorillas();
    resetPlayers();
  }

  private void resetGorillas() {
    gorillas.clear();
    for (NPC npc : NPCs.getAll()) {
      if (isNpcGorilla(npc.getId())) {
        gorillas.put(npc, new DemonicGorilla(npc));
      }
    }
  }

  private void resetPlayers() {
    //        memorizedPlayers.clear();
    //        memorizedPlayers.put(client.getLocalPlayer(), new
    // MemorizedPlayer(client.getLocalPlayer()));
    for (Player player : Players.getAll()) {
      memorizedPlayers.put(player, new MemorizedPlayer(player));
    }
  }

  private void checkGorillaAttackStyleSwitch(
      DemonicGorilla gorilla, final DemonicGorilla.AttackStyle... protectedStyles) {
    if (gorilla.getAttacksUntilSwitch() <= 0 || gorilla.getNextPosibleAttackStyles().isEmpty()) {
      gorilla.setNextPosibleAttackStyles(
          Arrays.stream(DemonicGorilla.ALL_REGULAR_ATTACK_STYLES)
              .filter(x -> Arrays.stream(protectedStyles).noneMatch(y -> x == y))
              .collect(Collectors.toList()));
      gorilla.setAttacksUntilSwitch(DemonicGorilla.ATTACKS_PER_SWITCH);
      gorilla.setChangedAttackStyleThisTick(true);
    }
  }

  private DemonicGorilla.AttackStyle getProtectedStyle(Player player) {
    HeadIcon headIcon = player.getOverheadIcon();
    if (headIcon == null) {
      return null;
    }
    switch (headIcon) {
      case MELEE:
        return DemonicGorilla.AttackStyle.MELEE;
      case RANGED:
        return DemonicGorilla.AttackStyle.RANGED;
      case MAGIC:
        return DemonicGorilla.AttackStyle.MAGIC;
      default:
        return null;
    }
  }

  private void onGorillaAttack(
      DemonicGorilla gorilla, final DemonicGorilla.AttackStyle attackStyle) {
    gorilla.setInitiatedCombat(true);

    Player target = (Player) gorilla.getNpc().getInteracting();

    DemonicGorilla.AttackStyle protectedStyle = null;
    if (target != null) {
      protectedStyle = getProtectedStyle(target);
    }
    boolean correctPrayer =
        target == null
            || // If player is out of memory, assume prayer was correct
            (attackStyle != null && attackStyle.equals(protectedStyle));

    if (attackStyle == DemonicGorilla.AttackStyle.BOULDER) {
      // The gorilla can't throw boulders when it's meleeing
      gorilla.setNextPosibleAttackStyles(
          gorilla.getNextPosibleAttackStyles().stream()
              .filter(x -> x != DemonicGorilla.AttackStyle.MELEE)
              .collect(Collectors.toList()));
    } else {
      if (correctPrayer) {
        gorilla.setAttacksUntilSwitch(gorilla.getAttacksUntilSwitch() - 1);
      } else {
        // We're not sure if the attack will hit a 0 or not,
        // so we don't know if we should decrease the counter or not,
        // so we keep track of the attack here until the damage splat
        // has appeared on the player.

        int damagesOnTick = Static.getClient().getTickCount();
        if (attackStyle == DemonicGorilla.AttackStyle.MAGIC) {
          MemorizedPlayer mp = memorizedPlayers.get(target);
          WorldArea lastPlayerArea = mp.getLastWorldArea();
          if (lastPlayerArea != null) {
            int dist = gorilla.getNpc().getWorldArea().distanceTo(lastPlayerArea);
            damagesOnTick +=
                (dist + DemonicGorilla.PROJECTILE_MAGIC_DELAY)
                    / DemonicGorilla.PROJECTILE_MAGIC_SPEED;
          }
        } else if (attackStyle == DemonicGorilla.AttackStyle.RANGED) {
          MemorizedPlayer mp = memorizedPlayers.get(target);
          WorldArea lastPlayerArea = mp.getLastWorldArea();
          if (lastPlayerArea != null) {
            int dist = gorilla.getNpc().getWorldArea().distanceTo(lastPlayerArea);
            damagesOnTick +=
                (dist + DemonicGorilla.PROJECTILE_RANGED_DELAY)
                    / DemonicGorilla.PROJECTILE_RANGED_SPEED;
          }
        }
        pendingAttacks.add(new PendingGorillaAttack(gorilla, attackStyle, target, damagesOnTick));
      }

      gorilla.setNextPosibleAttackStyles(
          gorilla.getNextPosibleAttackStyles().stream()
              .filter(x -> x == attackStyle)
              .collect(Collectors.toList()));

      if (gorilla.getNextPosibleAttackStyles().isEmpty()) {
        // Sometimes the gorilla can switch attack style before it's supposed to
        // if someone was fighting it earlier and then left, so we just
        // reset the counter in that case.

        gorilla.setNextPosibleAttackStyles(
            Arrays.stream(DemonicGorilla.ALL_REGULAR_ATTACK_STYLES)
                .filter(x -> x == attackStyle)
                .collect(Collectors.toList()));
        gorilla.setAttacksUntilSwitch(DemonicGorilla.ATTACKS_PER_SWITCH - (correctPrayer ? 1 : 0));
      }
    }

    checkGorillaAttackStyleSwitch(gorilla, protectedStyle);

    int tickCounter = Static.getClient().getTickCount();
    gorilla.setNextAttackTick(tickCounter + DemonicGorilla.ATTACK_RATE);
  }

  private void checkGorillaAttacks() {
    int tickCounter = Static.getClient().getTickCount();
    for (DemonicGorilla gorilla : gorillas.values()) {
      Player interacting = (Player) gorilla.getNpc().getInteracting();
      MemorizedPlayer mp = memorizedPlayers.get(interacting);

      if (gorilla.getLastTickInteracting() != null && interacting == null) {
        gorilla.setInitiatedCombat(false);
      } else if (mp != null
          && mp.getLastWorldArea() != null
          && !gorilla.isInitiatedCombat()
          && tickCounter < gorilla.getNextAttackTick()
          && gorilla.getNpc().getWorldArea().isInMeleeDistance(mp.getLastWorldArea())) {
        gorilla.setInitiatedCombat(true);
        gorilla.setNextAttackTick(tickCounter + 1);
      }

      int animationId = gorilla.getNpc().getAnimation();

      if (gorilla.isTakenDamageRecently() && tickCounter >= gorilla.getNextAttackTick() + 4) {
        // The gorilla was flinched, so its next attack gets delayed
        gorilla.setNextAttackTick(tickCounter + DemonicGorilla.ATTACK_RATE / 2);
        gorilla.setInitiatedCombat(true);

        if (mp != null
            && mp.getLastWorldArea() != null
            && !gorilla.getNpc().getWorldArea().isInMeleeDistance(mp.getLastWorldArea())
            && !gorilla.getNpc().getWorldArea().intersectsWith(mp.getLastWorldArea())) {
          // Gorillas stop meleeing when they get flinched
          // and the target isn't in melee distance
          gorilla.setNextPosibleAttackStyles(
              gorilla.getNextPosibleAttackStyles().stream()
                  .filter(x -> x != DemonicGorilla.AttackStyle.MELEE)
                  .collect(Collectors.toList()));
          if (interacting != null) {
            checkGorillaAttackStyleSwitch(
                gorilla, DemonicGorilla.AttackStyle.MELEE, getProtectedStyle(interacting));
          }
        }
      } else if (animationId != gorilla.getLastTickAnimation()) {
        if (animationId == AnimationID.DEMONIC_GORILLA_MELEE_ATTACK) {
          onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MELEE);
        } else if (animationId == AnimationID.DEMONIC_GORILLA_MAGIC_ATTACK) {
          onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MAGIC);
        } else if (animationId == AnimationID.DEMONIC_GORILLA_RANGED_ATTACK) {
          onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.RANGED);
        } else if (animationId == AnimationID.DEMONIC_GORILLA_AOE_ATTACK && interacting != null) {
          // Note that AoE animation is the same as prayer switch animation
          // so we need to check if the prayer was switched or not.
          // It also does this animation when it spawns, so
          // we need the interacting != null check.

          if (gorilla.getOverheadIcon() == gorilla.getLastTickOverheadIcon()) {
            // Confirmed, the gorilla used the AoE attack
            onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.BOULDER);
          } else {
            if (tickCounter >= gorilla.getNextAttackTick()) {
              gorilla.setChangedPrayerThisTick(true);

              // This part is more complicated because the gorilla may have
              // used an attack, but the prayer switch animation takes
              // priority over normal attack animations.

              int projectileId = gorilla.getRecentProjectileId();
              if (projectileId == ProjectileID.DEMONIC_GORILLA_MAGIC) {
                onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MAGIC);
              } else if (projectileId == ProjectileID.DEMONIC_GORILLA_RANGED) {
                onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.RANGED);
              } else if (mp != null) {
                WorldArea lastPlayerArea = mp.getLastWorldArea();
                if (lastPlayerArea != null
                    && recentBoulders.stream().anyMatch(x -> x.distanceTo(lastPlayerArea) == 0)) {
                  // A boulder started falling on the gorillas target,
                  // so we assume it was the gorilla who shot it
                  onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.BOULDER);
                } else if (!mp.getRecentHitsplats().isEmpty()) {
                  // It wasn't any of the three other attacks,
                  // but the player took damage, so we assume
                  // it's a melee attack
                  onGorillaAttack(gorilla, DemonicGorilla.AttackStyle.MELEE);
                }
              }
            }

            // The next attack tick is always delayed if the
            // gorilla switched prayer
            gorilla.setNextAttackTick(tickCounter + DemonicGorilla.ATTACK_RATE);
            gorilla.setChangedPrayerThisTick(true);
          }
        }
      }

      if (gorilla.getDisabledMeleeMovementForTicks() > 0) {
        gorilla.setDisabledMeleeMovementForTicks(gorilla.getDisabledMeleeMovementForTicks() - 1);
      } else if (gorilla.isInitiatedCombat()
          && gorilla.getNpc().getInteracting() != null
          && !gorilla.isChangedAttackStyleThisTick()
          && gorilla.getNextPosibleAttackStyles().size() >= 2
          && gorilla.getNextPosibleAttackStyles().stream()
              .anyMatch(x -> x == DemonicGorilla.AttackStyle.MELEE)) {
        // If melee is a possibility, we can check if the gorilla
        // is or isn't moving toward the player to determine if
        // it is actually attempting to melee or not.
        // We only run this check if the gorilla is in combat
        // because otherwise it attempts to travel to melee
        // distance before attacking its target.

        if (mp != null && mp.getLastWorldArea() != null && gorilla.getLastWorldArea() != null) {
          WorldArea predictedNewArea =
              gorilla
                  .getLastWorldArea()
                  .calculateNextTravellingPoint(
                      Static.getClient(),
                      mp.getLastWorldArea(),
                      true,
                      x -> {
                        // Gorillas can't normally walk through other gorillas
                        // or other players
                        final WorldArea area1 = new WorldArea(x, 1, 1);
                        return gorillas.values().stream()
                                .noneMatch(
                                    y -> {
                                      if (y == gorilla) {
                                        return false;
                                      }
                                      final WorldArea area2 =
                                          y.getNpc().getIndex() < gorilla.getNpc().getIndex()
                                              ? y.getNpc().getWorldArea()
                                              : y.getLastWorldArea();
                                      return area2 != null && area1.intersectsWith(area2);
                                    })
                            && memorizedPlayers.values().stream()
                                .noneMatch(
                                    y -> {
                                      final WorldArea area2 = y.getLastWorldArea();
                                      return area2 != null && area1.intersectsWith(area2);
                                    });

                        // There is a special case where if a player walked through
                        // a gorilla, or a player walked through another player,
                        // the tiles that were walked through becomes
                        // walkable, but I didn't feel like it's necessary to handle
                        // that special case as it should rarely happen.
                      });
          if (predictedNewArea != null) {
            int distance = gorilla.getNpc().getWorldArea().distanceTo(mp.getLastWorldArea());
            WorldPoint predictedMovement = predictedNewArea.toWorldPoint();
            if (distance <= DemonicGorilla.MAX_ATTACK_RANGE
                && mp.getLastWorldArea()
                    .hasLineOfSightTo(Static.getClient(), gorilla.getLastWorldArea())) {
              if (predictedMovement.distanceTo(gorilla.getLastWorldArea().toWorldPoint()) != 0) {
                if (predictedMovement.distanceTo(gorilla.getNpc().getWorldLocation()) == 0) {
                  gorilla.setNextPosibleAttackStyles(
                      gorilla.getNextPosibleAttackStyles().stream()
                          .filter(x -> x == DemonicGorilla.AttackStyle.MELEE)
                          .collect(Collectors.toList()));
                } else {
                  gorilla.setNextPosibleAttackStyles(
                      gorilla.getNextPosibleAttackStyles().stream()
                          .filter(x -> x != DemonicGorilla.AttackStyle.MELEE)
                          .collect(Collectors.toList()));
                }
              } else if (tickCounter >= gorilla.getNextAttackTick()
                  && gorilla.getRecentProjectileId() == -1
                  && recentBoulders.stream()
                      .noneMatch(x -> x.distanceTo(mp.getLastWorldArea()) == 0)) {
                gorilla.setNextPosibleAttackStyles(
                    gorilla.getNextPosibleAttackStyles().stream()
                        .filter(x -> x == DemonicGorilla.AttackStyle.MELEE)
                        .collect(Collectors.toList()));
              }
            }
          }
        }
      }

      if (gorilla.isTakenDamageRecently()) {
        gorilla.setInitiatedCombat(true);
      }

      if (gorilla.getOverheadIcon() != gorilla.getLastTickOverheadIcon()) {
        if (gorilla.isChangedAttackStyleLastTick() || gorilla.isChangedAttackStyleThisTick()) {
          // Apparently if it changes attack style and changes
          // prayer on the same tick or 1 tick apart, it won't
          // be able to move for the next 2 ticks if it attempts
          // to melee
          gorilla.setDisabledMeleeMovementForTicks(2);
        } else {
          // If it didn't change attack style lately,
          // it's only for the next 1 tick
          gorilla.setDisabledMeleeMovementForTicks(1);
        }
      }
      gorilla.setLastTickAnimation(gorilla.getNpc().getAnimation());
      gorilla.setLastWorldArea(gorilla.getNpc().getWorldArea());
      gorilla.setLastTickInteracting(gorilla.getNpc().getInteracting());
      gorilla.setTakenDamageRecently(false);
      gorilla.setChangedPrayerThisTick(false);
      gorilla.setChangedAttackStyleLastTick(gorilla.isChangedAttackStyleThisTick());
      gorilla.setChangedAttackStyleThisTick(false);
      gorilla.setLastTickOverheadIcon(gorilla.getOverheadIcon());
      gorilla.setRecentProjectileId(-1);
    }
  }

  @Subscribe
  private void onProjectileSpawned(ProjectileSpawned event) {
    final Projectile projectile = event.getProjectile();
    final int projectileId = projectile.getId();

    if (!DEMONIC_PROJECTILES.contains(projectileId)) {
      return;
    }

    final WorldPoint loc =
        WorldPoint.fromLocal(
            Static.getClient(),
            projectile.getX1(),
            projectile.getY1(),
            Static.getClient().getPlane());

    if (projectileId == ProjectileID.DEMONIC_GORILLA_BOULDER) {
      recentBoulders.add(loc);
    } else {
      for (DemonicGorilla gorilla : gorillas.values()) {
        if (gorilla.getNpc().getWorldLocation().distanceTo(loc) == 0) {
          gorilla.setRecentProjectileId(projectile.getId());
        }
      }
    }
  }

  private void checkPendingAttacks() {
    Iterator<PendingGorillaAttack> it = pendingAttacks.iterator();
    int tickCounter = Static.getClient().getTickCount();
    while (it.hasNext()) {
      PendingGorillaAttack attack = it.next();
      if (tickCounter >= attack.getFinishesOnTick()) {
        boolean shouldDecreaseCounter = false;
        DemonicGorilla gorilla = attack.getAttacker();
        MemorizedPlayer target = memorizedPlayers.get(attack.getTarget());
        if (target == null) {
          // Player went out of memory, so assume the hit was a 0
          shouldDecreaseCounter = true;
        } else if (target.getRecentHitsplats().isEmpty()) {
          // No hitsplats was applied. This may happen in some cases
          // where the player was out of memory while the
          // projectile was travelling. So we assume the hit was a 0.
          shouldDecreaseCounter = true;
        } else if (target.getRecentHitsplats().stream()
            .anyMatch(x -> x.getHitsplatType() == HitsplatID.BLOCK_ME)) {
          // A blue hitsplat appeared, so we assume the gorilla hit a 0
          shouldDecreaseCounter = true;
        }

        if (shouldDecreaseCounter) {
          gorilla.setAttacksUntilSwitch(gorilla.getAttacksUntilSwitch() - 1);
          checkGorillaAttackStyleSwitch(gorilla);
        }

        it.remove();
      }
    }
  }

  private void updatePlayers() {
    for (MemorizedPlayer mp : memorizedPlayers.values()) {
      mp.setLastWorldArea(mp.getPlayer().getWorldArea());
      mp.getRecentHitsplats().clear();
    }
  }

  @Subscribe
  private void onHitsplatApplied(HitsplatApplied event) {
    if (gorillas.isEmpty()) {
      return;
    }

    if (event.getActor() instanceof Player) {
      Player player = (Player) event.getActor();
      MemorizedPlayer mp = memorizedPlayers.get(player);
      if (mp != null) {
        mp.getRecentHitsplats().add(event.getHitsplat());
      }
    } else if (event.getActor() instanceof NPC) {
      DemonicGorilla gorilla = gorillas.get(event.getActor());
      int hitsplatType = event.getHitsplat().getHitsplatType();
      if (gorilla != null
          && (hitsplatType == HitsplatID.BLOCK_ME || hitsplatType == HitsplatID.DAMAGE_ME)) {
        gorilla.setTakenDamageRecently(true);
      }
    }
  }

  @Subscribe
  private void onGameStateChanged(GameStateChanged event) {
    GameState gs = event.getGameState();
    if (gs == GameState.LOGGING_IN || gs == GameState.CONNECTION_LOST || gs == GameState.HOPPING) {
      reset();
    }
  }

  @Subscribe
  private void onPlayerSpawned(PlayerSpawned event) {
    if (gorillas.isEmpty()) {
      return;
    }

    Player player = event.getPlayer();

    memorizedPlayers.put(player, new MemorizedPlayer(player));
  }

  @Subscribe
  private void onPlayerDespawned(PlayerDespawned event) {
    if (gorillas.isEmpty()) {
      return;
    }

    memorizedPlayers.remove(event.getPlayer());
  }

  @Subscribe
  private void onNpcSpawned(NpcSpawned event) {
    NPC npc = event.getNpc();
    if (isNpcGorilla(npc.getId())) {
      if (gorillas.isEmpty()) {
        // Players are not kept track of when there are no gorillas in
        // memory, so we need to add the players that were already in memory.
        resetPlayers();
      }

      gorillas.put(npc, new DemonicGorilla(npc));
    }
  }

  @Subscribe
  private void onNpcDespawned(NpcDespawned event) {
    if (currentGorilla != null && currentGorilla.getNpc().equals(event.getNpc())) {
      currentGorilla = null;
    }

    if (gorillas.remove(event.getNpc()) != null && gorillas.isEmpty()) {
      clear();
    }
  }
}
