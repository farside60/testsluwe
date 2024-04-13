package io.reisub.devious.farming;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Keybind;

@ConfigGroup("sluwefarming")
public interface Config extends net.runelite.client.config.Config {
  @ConfigSection(
      keyName = "seedsConfig",
      name = "Seeds",
      description = "Configure seeds",
      position = 0)
  String seedsConfig = "seedsConfig";

  @ConfigSection(
      keyName = "teleportOptionsConfig",
      name = "Teleport options",
      description = "Configure teleport options",
      position = 100)
  String teleportOptionsConfig = "teleportOptionsConfig";

  @ConfigSection(
      keyName = "oneClickConfig",
      name = "One click",
      description = "Configure one click mode",
      position = 200)
  String oneClickConfig = "oneClickConfig";

  @ConfigSection(
      keyName = "herbConfig",
      name = "Herbs",
      description = "Configure what herb patches to farm",
      position = 300)
  String herbConfig = "herbConfig";

  @ConfigItem(
      keyName = "manualMode",
      name = "Manual",
      description = "Use manual seeds mode, more info in the readme",
      section = "seedsConfig",
      position = 1)
  default boolean manualMode() {
    return false;
  }

  @ConfigItem(
      keyName = "seedsMode",
      name = "Seeds mode",
      description = "Choose which seeds to plant, more information in the readme",
      section = "seedsConfig",
      hide = "manualMode",
      position = 2)
  default SeedsMode seedsMode() {
    return SeedsMode.LOWEST_FIRST;
  }

  @ConfigItem(
      keyName = "manualSeeds",
      name = "Manual seeds",
      description = "Choose which seeds to use",
      hidden = true,
      unhide = "manualMode",
      section = "seedsConfig",
      position = 3)
  default String manualSeeds() {
    return "Snapdragon seed\nRanarr seed";
  }

  @ConfigItem(
      keyName = "manualSeedsSplit",
      name = "Split seeds evenly",
      description =
          "Split manually chosen seeds as evenly as possible, otherwise take as many as possible "
              + "of each until we have enough seeds",
      hidden = true,
      unhide = "manualMode",
      section = "seedsConfig",
      position = 4)
  default boolean manualSeedsSplit() {
    return true;
  }

  @ConfigItem(
      keyName = "diseaseFreeSeeds",
      name = "Disease free seeds",
      description = "Choose which seeds to use on disease free patches",
      section = "seedsConfig",
      position = 5)
  default String diseaseFreeSeeds() {
    return "Snapdragon seed\nRanarr seed";
  }

  @ConfigItem(
      keyName = "seedsToKeep",
      name = "Seeds to keep",
      description =
          "Choose which seeds to keep some of in your bank, useful for seeds used in farming "
              + "contracts",
      section = "seedsConfig",
      position = 6)
  default String seedsToKeep() {
    return "Snapdragon seed\nCadantine seed\nLantadyme seed\nDwarf weed seed\nTorstol seed";
  }

  @ConfigItem(
      keyName = "amountToKeep",
      name = "Amount to keep",
      description = "Choose how many seeds to keep of each kind",
      section = "seedsConfig",
      position = 7)
  default int amountToKeep() {
    return 10;
  }

  @ConfigItem(
      keyName = "limpwurt",
      name = "Limpwurt",
      description = "Enable to also plant and harvest Limpwurt roots",
      section = "seedsConfig",
      position = 8)
  default boolean limpwurt() {
    return false;
  }

  @ConfigItem(
      keyName = "cleanHerbs",
      name = "Clean herbs",
      description = "Enable to clean any herbs we harvest",
      section = "seedsConfig",
      position = 8)
  default boolean cleanHerbs() {
    return false;
  }

  @ConfigItem(
      keyName = "useExplorersRing",
      name = "Use Explorer's ring",
      description = "Use the Explorer's ring to teleport to Falador",
      section = "teleportOptionsConfig",
      position = 101,
      hidden = true,
      unhide = "faladorHerb")
  default boolean useExplorersRing() {
    return false;
  }

  @ConfigItem(
      keyName = "catherbyThroughHouse",
      name = "Catherby through house",
      description = "Teleport to Catherby through your house",
      section = "teleportOptionsConfig",
      position = 102,
      hidden = true,
      unhide = "catherbyHerb")
  default boolean catherbyThroughHouse() {
    return false;
  }

  @ConfigItem(
      keyName = "useArdougneCloak",
      name = "Use Ardougne cloak",
      description = "Use the Ardougne cloak to teleport to Ardougne",
      section = "teleportOptionsConfig",
      position = 103,
      hidden = true,
      unhide = "ardougneHerb")
  default boolean useArdougneCloak() {
    return false;
  }

  @ConfigItem(
      keyName = "useXericsTalisman",
      name = "Use Xeric's talisman",
      description = "Use Xeric's talisman to teleport to Hosidius",
      section = "teleportOptionsConfig",
      position = 104,
      hidden = true,
      unhide = "hosidiusHerb")
  default boolean useXericsTalisman() {
    return false;
  }

  @ConfigItem(
      keyName = "varlamoreRequireStaff",
      name = "Require fairy ring staff",
      description = "You can disable this if you don't require a staff to access the fairy rings.",
      section = "teleportOptionsConfig",
      position = 105,
      hidden = true,
      unhide = "varlamoreHerb")
  default boolean varlamoreRequireStaff() {
    return true;
  }

  @ConfigItem(
      keyName = "oneClickMode",
      name = "One click mode",
      description = "Enable one click mode to quickly do farming actions manually",
      section = "oneClickConfig",
      position = 201)
  default boolean oneClickMode() {
    return false;
  }

  @ConfigItem(
      keyName = "oneClickNote",
      name = "One click note",
      description = "Enable one click mode on any produce that can be noted",
      hidden = true,
      unhide = "oneClickMode",
      section = "oneClickConfig",
      position = 202)
  default boolean oneClickNote() {
    return false;
  }

  @ConfigItem(
      keyName = "oneClickCompostProduce",
      name = "One click compost produce",
      description = "Choose what produce to put in the compost bin",
      hidden = true,
      unhide = "oneClickMode",
      section = "oneClickConfig",
      position = 203)
  default String oneClickCompostProduce() {
    return "Watermelon\nPineapple";
  }

  @ConfigItem(
      keyName = "faladorHerb",
      name = "Falador",
      description = "Enable patch",
      section = "herbConfig",
      position = 301)
  default boolean faladorHerb() {
    return true;
  }

  @ConfigItem(
      keyName = "portPhasmatysHerb",
      name = "Port Phasmatys",
      description = "Enable patch",
      section = "herbConfig",
      position = 302)
  default boolean portPhasmatysHerb() {
    return true;
  }

  @ConfigItem(
      keyName = "catherbyHerb",
      name = "Catherby",
      description = "Enable patch",
      section = "herbConfig",
      position = 303)
  default boolean catherbyHerb() {
    return true;
  }

  @ConfigItem(
      keyName = "ardougneHerb",
      name = "Ardougne",
      description = "Enable patch",
      section = "herbConfig",
      position = 304)
  default boolean ardougneHerb() {
    return true;
  }

  @ConfigItem(
      keyName = "hosidiusHerb",
      name = "Hosidius",
      description = "Enable patch",
      section = "herbConfig",
      position = 305)
  default boolean hosidiusHerb() {
    return true;
  }

  @ConfigItem(
      keyName = "guildHerb",
      name = "Farming Guild",
      description = "Enable patch",
      section = "herbConfig",
      position = 306)
  default boolean guildHerb() {
    return false;
  }

  @ConfigItem(
      keyName = "trollStrongholdHerb",
      name = "Troll Stronghold",
      description = "Enable patch",
      section = "herbConfig",
      position = 307)
  default boolean trollStrongholdHerb() {
    return false;
  }

  @ConfigItem(
      keyName = "weissHerb",
      name = "Weiss",
      description = "Enable patch",
      section = "herbConfig",
      position = 308)
  default boolean weissHerb() {
    return false;
  }

  @ConfigItem(
      keyName = "harmonyHerb",
      name = "Harmony Island",
      description = "Enable patch",
      section = "herbConfig",
      position = 309)
  default boolean harmonyHerb() {
    return false;
  }

  @ConfigItem(
      keyName = "varlamoreHerb",
      name = "Varlamore",
      description =
          "Enable patch. Note: this requires fairy rings and the Varlamore code to "
              + "already have been used once before.",
      section = "herbConfig",
      position = 310)
  default boolean varlamoreHerb() {
    return false;
  }

  @ConfigItem(
      keyName = "herbOrder",
      name = "Order",
      description =
          "Order in which to do the locations. Any enabled location not in this order list will "
              + "simply be added to the end.",
      section = "herbConfig",
      position = 311)
  default String herbOrder() {
    return "Farming Guild\nArdougne\nCatherby\nFalador\nPort Phasmatys\nVarlamore\nHosidius\n"
        + "Harmony Island\nTroll Stronghold\nWeiss";
  }

  @ConfigItem(
      keyName = "barbarianFarming",
      name = "Barbarian farming",
      description = "Enabling this will omit getting a seed dibber from the leprechaun.",
      position = Integer.MAX_VALUE - 4)
  default boolean barbarianFarming() {
    return false;
  }

  @ConfigItem(
      keyName = "compost",
      name = "Compost",
      description = "Select the compost bucket you'd like to use.",
      position = Integer.MAX_VALUE - 3)
  default Compost compost() {
    return Compost.BOTTOMLESS;
  }

  @ConfigItem(
      keyName = "farmingHotkey",
      name = "Start hotkey",
      description = "Choose the hotkey to start a farm run",
      position = Integer.MAX_VALUE - 2)
  default Keybind farmingHotkey() {
    return new Keybind(KeyEvent.VK_F6, InputEvent.CTRL_DOWN_MASK);
  }

  @ConfigItem(
      keyName = "harvestAndCompostHotkey",
      name = "Harvest and compost hotkey",
      description =
          "Pressing this hotkey will harvest the nearest allotment patch and put the "
              + "produce in the compost bin at the same time. Requires at least 1 of the produce "
              + "in the inventory already",
      position = Integer.MAX_VALUE - 1)
  default Keybind harvestAndCompostHotkey() {
    return new Keybind(KeyEvent.VK_F7, InputEvent.CTRL_DOWN_MASK);
  }

  @ConfigItem(
      keyName = "startButton",
      name = "Start/Stop",
      description = "Start the script",
      position = Integer.MAX_VALUE)
  default Button startButton() {
    return new Button();
  }
}
