package io.reisub.devious.tithefarm;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwetithefarm")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "depositAmount",
      name = "Deposit amount",
      description = "Choose the amount of fruit at which you'd like to deposit",
      position = 0)
  default int depositAmount() {
    return 100;
  }

  @ConfigItem(
      keyName = "buyRewards",
      name = "Buy rewards",
      description = "Buy rewards at 900 points",
      position = 1)
  default boolean buyRewards() {
    return false;
  }

  @ConfigItem(
      keyName = "buyGricollersCan",
      name = "Buy Gricoller's can",
      description = "Buy Gricoller's can",
      position = 2,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyGricollersCan() {
    return true;
  }

  @ConfigItem(
      keyName = "buyAutoWeed",
      name = "Buy Auto Weed",
      description = "Buy Auto Weed",
      position = 3,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyAutoWeed() {
    return true;
  }

  @ConfigItem(
      keyName = "buyFarmersTorso",
      name = "Buy Farmer's Torso",
      description = "Buy Farmer's Torso",
      position = 4,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyFarmersTorso() {
    return true;
  }

  @ConfigItem(
      keyName = "buyFarmersLegs",
      name = "Buy Farmer's Legs",
      description = "Buy Farmer's Legs",
      position = 5,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyFarmersLegs() {
    return true;
  }

  @ConfigItem(
      keyName = "buyFarmersHat",
      name = "Buy Farmer's Hat",
      description = "Buy Farmer's HAt",
      position = 6,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyFarmersHat() {
    return true;
  }

  @ConfigItem(
      keyName = "buyFarmersBoots",
      name = "Buy Farmer's Boots",
      description = "Buy Farmer's Boots",
      position = 7,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyFarmersBoots() {
    return true;
  }

  @ConfigItem(
      keyName = "buyHerbSack",
      name = "Buy Herb Sack",
      description = "Buy Herb Sack",
      position = 8,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyHerbSack() {
    return true;
  }

  @ConfigItem(
      keyName = "buySeedBox",
      name = "Buy Seed Box",
      description = "Buy Seed Box",
      position = 9,
      hidden = true,
      unhide = "buyRewards")
  default boolean buySeedBox() {
    return true;
  }

  @ConfigItem(
      keyName = "buyHerbBox",
      name = "Buy Herb Box",
      description = "Buy Herb Box",
      position = 10,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyHerbBox() {
    return false;
  }

  @ConfigItem(
      keyName = "buyGrapeSeed",
      name = "Buy Grape Seed",
      description = "Buy Grape Seed",
      position = 11,
      hidden = true,
      unhide = "buyRewards")
  default boolean buyGrapeSeed() {
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
