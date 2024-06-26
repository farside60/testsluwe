package io.reisub.devious.farming;

import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.api.Interact;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.enums.FairyRingCode;
import io.reisub.devious.utils.enums.HouseTeleport;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.MenuAction;
import net.runelite.api.Varbits;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.api.widgets.Widgets;

@RequiredArgsConstructor
@Getter
@SuppressWarnings("checkstyle:Indentation")
public enum Location {
  ARDOUGNE(
      "Ardougne",
      new WorldPoint(2670, 3376, 0),
      10548,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> {
        if (!Interact.interactWithInventoryOrEquipment(
            Constants.ARDOUGNE_CLOAK_IDS, "Farm Teleport", "Ardougne Farm", 0)) {
          Magic.cast(SpellBook.Standard.ARDOUGNE_TELEPORT);
        }

        return true;
      }),
  CATHERBY(
      "Catherby",
      new WorldPoint(2813, 3465, 0),
      11062,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> {
        if (Farming.catherbyThroughHouse) {
          return SluweMovement.teleportThroughHouse(HouseTeleport.CATHERBY);
        }

        return false;
      }),
  FALADOR(
      "Falador",
      new WorldPoint(3058, 3310, 0),
      12083,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> {
        if (!Interact.interactWithInventoryOrEquipment(
            Constants.EXPLORERS_RING_IDS, "Teleport", null, 0)) {
          Magic.cast(SpellBook.Standard.FALADOR_TELEPORT);
        }

        return true;
      }),
  FARMING_GUILD(
      "Farming Guild",
      new WorldPoint(1239, 3728, 0),
      4922,
      Varbits.FARMING_4775,
      Varbits.FARMING_7906,
      () -> {
        Item necklace = Inventory.getFirst(Predicates.ids(Constants.SKILL_NECKLACE_IDS));

        if (necklace == null) {
          // TODO
        } else {
          necklace.interact("Rub");
          Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(187, 3)), 5);

          Widget farmingGuild = Widgets.get(187, 3, 5);
          if (farmingGuild != null) {
            farmingGuild.interact(
                0,
                MenuAction.WIDGET_CONTINUE.getId(),
                farmingGuild.getIndex(),
                farmingGuild.getId());
            return true;
          }
        }

        return false;
      }),
  HARMONY_ISLAND(
      "Harmony Island",
      new WorldPoint(3790, 2839, 0),
      15148,
      Varbits.FARMING_4772,
      0,
      () -> SluweMovement.teleportThroughHouse(HouseTeleport.HARMONY_ISLAND)),
  HOSIDIUS(
      "Hosidius",
      new WorldPoint(1740, 3550, 0),
      6967,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> {
        Item talisman = Inventory.getFirst(ItemID.XERICS_TALISMAN);

        if (talisman == null) {
          final Item cape = Inventory.getFirst(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS));

          if (cape == null) {
            Widget widget = Widgets.get(SpellBook.Standard.TELEPORT_TO_HOUSE.getWidget());
            if (widget != null) {
              widget.interact("Outside");
              return true;
            }
          } else {
            cape.interact("Teleport");
            Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(187, 3)), 5);

            final Widget hosidius = Widgets.get(187, 3, 4);

            if (hosidius != null) {
              hosidius.interact(0, MenuAction.WIDGET_CONTINUE.getId());
            }
          }
        } else {
          // TODO
        }

        return false;
      }),
  PORT_PHASMATYS(
      "Port Phasmatys",
      new WorldPoint(3606, 3531, 0),
      14391,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> {
        Item ectophial = Inventory.getFirst(ItemID.ECTOPHIAL, ItemID.ECTOPHIAL_4252);
        if (ectophial == null) {
          return false;
        }

        ectophial.interact("Empty");

        Time.sleepTicksUntil(() -> Inventory.contains(ItemID.ECTOPHIAL_4252), 10);
        return Time.sleepTicksUntil(() -> Inventory.contains(ItemID.ECTOPHIAL), 10);
      }),
  TROLL_STRONGHOLD(
      "Troll Stronghold",
      new WorldPoint(2828, 3694, 0),
      11321,
      Varbits.FARMING_4771,
      0,
      () -> SluweMovement.teleportThroughHouse(HouseTeleport.TROLL_STRONGHOLD)),
  WEISS(
      "Weiss",
      new WorldPoint(2847, 3935, 0),
      11325,
      Varbits.FARMING_4771,
      0,
      () -> SluweMovement.teleportThroughHouse(HouseTeleport.WEISS, 70)),
  VARLAMORE(
      "Varlamore",
      new WorldPoint(1587, 3100, 0),
      6192,
      Varbits.FARMING_4774,
      Varbits.FARMING_4773,
      () -> SluweMovement.useFairyRing(FairyRingCode.AJP));

  private final String name;
  private final WorldPoint patchPoint;
  private final int regionId;
  private final int herbVarbit;
  private final int flowerVarbit;
  private final Teleportable teleportable;

  @Setter private boolean skip;

  public boolean hasFlowerPatch() {
    return flowerVarbit != 0;
  }

  public boolean isEnabled(Config config) {
    switch (this) {
      case FARMING_GUILD:
        return config.guildHerb();
      case ARDOUGNE:
        return config.ardougneHerb();
      case CATHERBY:
        return config.catherbyHerb();
      case FALADOR:
        return config.faladorHerb();
      case PORT_PHASMATYS:
        return config.portPhasmatysHerb();
      case HOSIDIUS:
        return config.hosidiusHerb();
      case HARMONY_ISLAND:
        return config.harmonyHerb();
      case TROLL_STRONGHOLD:
        return config.trollStrongholdHerb();
      case WEISS:
        return config.weissHerb();
      case VARLAMORE:
        return config.varlamoreHerb();
      default:
    }

    return false;
  }

  public interface Teleportable {
    boolean teleport();
  }
}
