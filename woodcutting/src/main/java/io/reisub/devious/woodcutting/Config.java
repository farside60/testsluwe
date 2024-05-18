package io.reisub.devious.woodcutting;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;

@ConfigGroup("sluwewoodcutting")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "location",
      name = "Location",
      description = "Choose what to chop and where",
      position = 0)
  default Location location() {
    return Location.YEW_WOODCUTTING_GUILD;
  }

  @ConfigItem(keyName = "drop", name = "Drop", description = "Drop logs", position = 1)
  default boolean drop() {
    return false;
  }

  @ConfigItem(keyName = "burn", name = "Burn", description = "Burn logs", position = 2)
  default boolean burn() {
    return false;
  }

  @ConfigItem(
      keyName = "birdNests",
      name = "Pick up bird nests",
      description = "Pick up bird nests",
      position = 3)
  default boolean birdNests() {
    return true;
  }

  @ConfigItem(
      keyName = "onlyPickUpOurs",
      name = "Only pick up our nests",
      description = "Don't pick up other people's nests, useful for ironmen",
      hidden = true,
      unhide = "birdNests",
      position = 4)
  default boolean onlyPickUpOurs() {
    return false;
  }

  @ConfigSection(
      name = "Forestry",
      description = "Configuration options for Forestry",
      position = 10)
  String forestrySection = "forestry";

  @ConfigItem(
      keyName = "forestryRoots",
      name = "Roots",
      description = "Do the rising roots event",
      position = 11,
      section = forestrySection)
  default boolean forestryRoots() {
    return true;
  }

  @ConfigItem(
      keyName = "forestryPoacher",
      name = "Poacher",
      description = "Do the poacher event",
      position = 12,
      section = forestrySection)
  default boolean forestryPoacher() {
    return true;
  }

  @ConfigItem(
      keyName = "forestryPheasants",
      name = "Pheasants",
      description = "Do the pheasants event",
      position = 13,
      section = forestrySection)
  default boolean forestryPheasants() {
    return true;
  }

  @ConfigItem(
      keyName = "forestryBeehive",
      name = "Beehive",
      description = "Do the beehive event",
      position = 14,
      section = forestrySection)
  default boolean forestryBeehive() {
    return true;
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
