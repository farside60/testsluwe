package io.reisub.devious.autopickup;

import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("sluweautopickup")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "items",
      name = "Items",
      description = "List of items, can use IDs or names, separated by newline, comma or semicolon",
      position = 0)
  default String items() {
    return "Logs\nOak plank";
  }

  @ConfigItem(
      keyName = "addLocationHotkey",
      name = "Add location hotkey",
      description =
          "Pressing this hotkey will add the current hovered tile to the list of of locations "
              + "to check for items. If you don't add any tiles, the plugin will pick up from any "
              + "tile",
      position = 1)
  default Keybind addLocationHotkey() {
    return new Keybind(KeyEvent.VK_A, InputEvent.CTRL_DOWN_MASK & InputEvent.SHIFT_DOWN_MASK);
  }

  @ConfigItem(
      keyName = "bankLocation",
      name = "Bank location",
      description = "Point near the bank in format x,y,z.",
      position = 2)
  default String bankLocation() {
    return "0,0,0";
  }

  @ConfigItem(
      keyName = "amount",
      name = "Amount",
      description = "Amount of items to pick up.",
      position = 3)
  default int amount() {
    return 0;
  }

  @ConfigItem(
      keyName = "enableFreeWorlds",
      name = "Enable F2P worlds",
      description = "Only enable this is if you're picking up F2P items in a F2P area.",
      position = 4)
  default boolean enableFreeWorlds() {
    return false;
  }

  @ConfigItem(
      keyName = "burn",
      name = "Burn",
      description = "Burn instead of picking up, only useful for logs.",
      position = 10)
  default boolean burn() {
    return false;
  }

  @ConfigItem(
      keyName = "firemakingLevel",
      name = "Firemaking level",
      description = "Set the firemaking level at which to stop burning.",
      position = 11,
      hidden = true,
      unhide = "burn")
  default int firemakingLevel() {
    return 15;
  }

  @ConfigItem(
      keyName = "pickUpAshes",
      name = "Pickup ashes",
      description = "Pick up ashes.",
      position = 12,
      hidden = true,
      unhide = "burn")
  default boolean pickUpAshes() {
    return false;
  }

  @ConfigItem(
      keyName = "ashAmount",
      name = "Ash amount",
      description = "Amount of ashes to pick up.",
      position = 13,
      hidden = true,
      unhide = "pickUpAshes")
  default int ashAmount() {
    return 4;
  }

  @ConfigItem(
      keyName = "fletchArrowShafts",
      name = "Fletch arrow shafts",
      description =
          "Fletch arrow shafts from logs, only useful when picking up logs. "
              + "Only normal logs supported.",
      position = 20)
  default boolean fletchArrowShafts() {
    return false;
  }

  @ConfigItem(
      keyName = "arrowShaftAmount",
      name = "Arrow shaft amount",
      description = "Specify how many arrow shafts to fletch.",
      position = 21,
      hidden = true,
      unhide = "fletchArrowShafts")
  default int arrowShaftAmount() {
    return 1000;
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
