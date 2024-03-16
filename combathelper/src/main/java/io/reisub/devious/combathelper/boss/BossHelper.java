package io.reisub.devious.combathelper.boss;

import com.openosrs.client.util.WeaponStyle;
import io.reisub.devious.alchemicalhydra.SluweAlchemicalHydra;
import io.reisub.devious.alchemicalhydra.entity.Hydra;
import io.reisub.devious.cerberus.SluweCerberus;
import io.reisub.devious.cerberus.domain.Cerberus;
import io.reisub.devious.cerberus.domain.CerberusAttack;
import io.reisub.devious.combathelper.CombatHelper;
import io.reisub.devious.combathelper.Helper;
import io.reisub.devious.gauntletextended.SluweGauntletExtended;
import io.reisub.devious.grotesqueguardians.SluweGrotesqueGuardians;
import io.reisub.devious.grotesqueguardians.entity.Dusk;
import io.reisub.devious.utils.enums.SluwePrayer;
import io.reisub.devious.zulrah.SluweZulrah;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.HeadIcon;
import net.runelite.api.Item;
import net.runelite.api.NpcID;
import net.runelite.api.Prayer;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ProjectileSpawned;
import net.runelite.client.eventbus.Subscribe;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;

@Singleton
public class BossHelper extends Helper {

  private static final int ZULRAH_RANGED_ANIMATION = 1044;
  @Inject
  private CombatHelper plugin;
  @Inject
  private SluweAlchemicalHydra alchemicalHydraPlugin;
  @Inject
  private SluweCerberus cerberusPlugin;
  @Inject
  private SluweZulrah zulrahPlugin;
  @Inject
  private SluweGauntletExtended gauntletPlugin;
  @Inject
  private SluweGrotesqueGuardians grotesqueGuardiansPlugin;
  private WeaponStyle currentStyle;

  @Subscribe(priority = 95)
  private void onGameTick(GameTick event) {
    currentStyle = Combat.getCurrentWeaponStyle();

    if (config.alchemicalHydraPrayerFlick() && alchemicalHydraPlugin.getHydra() != null) {
      alchemicalHydraFlick();
    }

    if (config.cerberusPrayerFlick() && cerberusPlugin.getCerberus() != null) {
      cerberusFlick();
    }

    if (config.autoSwapZulrah() && !zulrahPlugin.getZulrahData().isEmpty()) {
      zulrahSwap();
    }

    if (config.zulrahPrayerFlick() && !zulrahPlugin.getZulrahData().isEmpty()) {
      zulrahFlick();
    }

    if (config.hunleffPrayerFlick()
        && gauntletPlugin.isInHunllef()
        && gauntletPlugin.getHunllef() != null) {
      hunleffFlick();
    }

    if (config.autoSwapHunleff()
        && gauntletPlugin.isInHunllef()
        && gauntletPlugin.getHunllef() != null) {
      hunleffSwap();
    }

    if (config.gauntletPrayerFlick()
        && gauntletPlugin.isInGauntlet()
        && !gauntletPlugin.isInHunllef()) {
      gauntletPrayerFlick();
    }

    if (config.autoSwapGrotesqueGuardians() && grotesqueGuardiansPlugin.isOnRoof()) {
      grotesqueGuardiansSwap();
    }

    if (config.grotesqueGuardiansPrayerFlick() && grotesqueGuardiansPlugin.isOnRoof()) {
      grotesqueGuardiansFlick();
    }
  }

  @Subscribe(priority = 80)
  private void onProjectileSpawned(ProjectileSpawned event) {
    if (config.zulrahAutoVengeance() && event.getProjectile().getId() == ZULRAH_RANGED_ANIMATION) {
      zulrahPlugin
          .getZulrahData()
          .forEach(
              data ->
                  data.getCurrentZulrahNpc()
                      .ifPresent(
                          zulrah -> {
                            if (zulrah.getType().getSkill() == Skill.MAGIC) {
                              plugin.getMiscHelper().castVengeance();
                            }
                          }));
    }
  }

  private void alchemicalHydraFlick() {
    final Hydra hydra = alchemicalHydraPlugin.getHydra();

    if (hydra == null || hydra.getNextAttack() == null
        || hydra.getNextAttack().getPrayer() == null) {
      return;
    }

    switch (hydra.getNextAttack().getPrayer()) {
      case PROTECT_FROM_MAGIC:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
        break;
      case PROTECT_FROM_MISSILES:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        break;
      case PROTECT_FROM_MELEE:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
        break;
      default:
        break;
    }
  }

  private void cerberusFlick() {
    List<CerberusAttack> upcomingAttacks = cerberusPlugin.getUpcomingAttacks();
    Prayer prayer = null;

    if (upcomingAttacks != null && !upcomingAttacks.isEmpty()) {
      prayer = upcomingAttacks.get(0).getAttack().getPrayer();
    }

    if (prayer == null) {
      prayer = cerberusPlugin.getPrayer();
    }

    if (prayer != null) {
      switch (prayer) {
        case PROTECT_FROM_MELEE:
          plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
          break;
        case PROTECT_FROM_MISSILES:
          if (cerberusPlugin.getUpcomingAttacks().get(0).getAttack() == Cerberus.Attack.GHOST_RANGED
              || cerberusPlugin.getCerberus().getLastTripleAttack() != null) {
            plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
          }
          break;
        case PROTECT_FROM_MAGIC:
          plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
          break;
        default:
      }
    }
  }

  private void zulrahSwap() {
    zulrahPlugin
        .getZulrahData()
        .forEach(
            data ->
                data.getCurrentPhase()
                    .ifPresent(
                        phase -> {
                          if (phase.getZulrahNpc().isJad()) {
                            if (currentStyle != WeaponStyle.MAGIC) {
                              plugin.getSwapHelper().swap(true, false, WeaponStyle.MAGIC);
                            }
                          } else {
                            switch (phase.getZulrahNpc().getType()) {
                              case MELEE:
                              case RANGE:
                                if (currentStyle != WeaponStyle.MAGIC) {
                                  plugin.getSwapHelper().swap(true, false, WeaponStyle.MAGIC);
                                }
                                break;
                              case MAGIC:
                                if (currentStyle != WeaponStyle.RANGE) {
                                  plugin.getSwapHelper().swap(true, false, WeaponStyle.RANGE);
                                }
                                break;
                              default:
                            }
                          }
                        }));
  }

  private void zulrahFlick() {
    AtomicReference<Prayer> prayer = new AtomicReference<>();

    zulrahPlugin
        .getZulrahData()
        .forEach(
            data ->
                data.getCurrentPhasePrayer()
                    .ifPresentOrElse(
                        prayer::set,
                        () -> data.getNextPhase()
                            .ifPresent(
                                phase -> {
                                  prayer.set(phase.getAttributes().getPrayer());
                                })));

    if (prayer.get() == null) {
      return;
    }

    switch (prayer.get()) {
      case PROTECT_FROM_MAGIC:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
        break;
      case PROTECT_FROM_MISSILES:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        break;
      default:
        break;
    }
  }

  private void hunleffFlick() {
    final Prayer prayer = gauntletPlugin.getHunllef().getAttackPhase().getPrayer();

    if (prayer == null) {
      return;
    }

    switch (prayer) {
      case PROTECT_FROM_MAGIC:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
        break;
      case PROTECT_FROM_MISSILES:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        break;
      default:
        break;
    }
  }

  private void hunleffSwap() {
    final HeadIcon headIcon =
        gauntletPlugin.getHunllef().getNpc().getComposition().getOverheadIcon();

    if (headIcon == null) {
      return;
    }

    final Item weapon = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);

    if (config.hunleff51()
        && (Equipment.contains(Predicates.nameContains("bow"))
        || Inventory.contains(Predicates.nameContains("bow")))
        && (Equipment.contains(Predicates.nameContains("staff"))
        || Inventory.contains(Predicates.nameContains("staff")))) {
      // we have bow and staff, doing 5:1 method
      final int playerAttackCount = gauntletPlugin.getHunllef().getPlayerAttackCount();

      if (Equipment.contains(Predicates.nameContains("bow"))
          && playerAttackCount == 1) {
        switch (headIcon) {
          case MELEE:
            Inventory.getFirst(Predicates.nameContains("staff")).interact("Wield");
            break;
          case MAGIC:
            final Item halberd = Inventory.getFirst(Predicates.nameContains("halberd"));

            if (halberd == null) {
              Equipment.getFirst(Predicates.nameContains("bow")).interact("Remove");
            } else {
              halberd.interact("Wield");
            }
            break;
          default:
        }
      } else if (!Equipment.contains(Predicates.nameContains("bow"))
          && playerAttackCount != 1) {
        Inventory.getFirst(Predicates.nameContains("bow")).interact("Wield");
      }
    } else {
      if (weapon == null) {
        return;
      }

      switch (headIcon) {
        case MELEE:
          if (weapon.getName().contains("halberd")) {
            plugin.getSwapHelper()
                .swap(true, false, WeaponStyle.RANGE, WeaponStyle.MAGIC);
          }
          break;
        case RANGED:
          if (weapon.getName().contains("bow")) {
            plugin.getSwapHelper()
                .swap(true, false, WeaponStyle.MELEE, WeaponStyle.MAGIC);
          }
          break;
        case MAGIC:
          if (weapon.getName().contains("staff")) {
            plugin.getSwapHelper()
                .swap(true, false, WeaponStyle.RANGE, WeaponStyle.MELEE);
          }
          break;
        default:
      }
    }
  }

  private void gauntletPrayerFlick() {
    if (Players.getLocal().getInteracting() == null) {
      return;
    }

    final int targetId = Players.getLocal().getInteracting().getId();

    switch (targetId) {
      case NpcID.CRYSTALLINE_DRAGON:
      case NpcID.CORRUPTED_DRAGON:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MAGIC, false);
        break;
      case NpcID.CRYSTALLINE_DARK_BEAST:
      case NpcID.CORRUPTED_DARK_BEAST:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        break;
      default:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
    }
  }

  private void grotesqueGuardiansSwap() {
    final Dusk dusk = grotesqueGuardiansPlugin.getDusk();

    if (dusk == null) {
      return;
    }

    switch (dusk.getPhase()) {
      case PHASE_1:
      case PHASE_3:
        if (currentStyle != WeaponStyle.RANGE) {
          plugin.getSwapHelper().swap(true, false, WeaponStyle.RANGE);
        }
        break;
      case PHASE_2:
      case PHASE_4:
        if (currentStyle != WeaponStyle.MELEE) {
          plugin.getSwapHelper().swap(true, false, WeaponStyle.MELEE);
        }
        break;
      default:
        break;
    }
  }

  private void grotesqueGuardiansFlick() {
    final Dusk dusk = grotesqueGuardiansPlugin.getDusk();

    if (dusk == null) {
      return;
    }

    switch (dusk.getPhase()) {
      case PHASE_1:
      case PHASE_3:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        break;
      case PHASE_2:
        plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
        break;
      case PHASE_4:
        if (dusk.getLastAttackPrayer() == Prayer.PROTECT_FROM_MELEE) {
          plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MELEE, false);
        } else {
          plugin.getPrayerHelper().setPrayer(SluwePrayer.PROTECT_FROM_MISSILES, false);
        }
        break;
      default:
        break;
    }
  }
}
