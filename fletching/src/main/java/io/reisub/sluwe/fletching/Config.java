package io.reisub.sluwe.fletching;

import io.reisub.sluwe.fletching.data.Gem;
import io.reisub.sluwe.fletching.data.Log;
import io.reisub.sluwe.fletching.data.Metal;
import io.reisub.sluwe.fletching.data.Product;
import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwefletching")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "product",
      name = "Product",
      description = "Select what to make",
      position = 0)
  default Product product() {
    return Product.ARROW_SHAFTS;
  }

  @ConfigItem(
      keyName = "logType",
      name = "Log type",
      description = "Select the log type",
      position = 1)
  default Log logType() {
    return Log.NORMAL;
  }

  @ConfigItem(
      keyName = "metalType",
      name = "Metal type",
      description = "Select the metal type",
      position = 2)
  default Metal metalType() {
    return Metal.BRONZE;
  }

  @ConfigItem(
      keyName = "gemType",
      name = "Gem type",
      description = "Select the gem type",
      position = 3)
  default Gem gemType() {
    return Gem.OPAL;
  }

  @ConfigItem(
      keyName = "amountOfLogsToUse",
      name = "Amount of logs to use",
      description = "Limit the amount of logs to use for fletching, 0 for unlimited",
      position = 4)
  default int amountOfLogsToUse() {
    return 0;
  }

  @ConfigItem(
      keyName = "useDragonBolts",
      name = "Use dragon bolts",
      description = "Use dragon bolts when adding gem tips",
      position = 5)
  default boolean useDragonBolts() {
    return false;
  }

  @ConfigItem(
      keyName = "farmSeaweedSpores",
      name = "Farm seaweed spores",
      description = "Fletch underwater at Fossil island and pick up any seaweed spores that spawn",
      position = 6)
  default boolean farmSeaweedSpores() {
    return false;
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
