package io.reisub.devious.smelter;

import net.runelite.client.config.Button;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("sluwesmelter")
public interface Config extends net.runelite.client.config.Config {
  @ConfigItem(
      keyName = "location",
      name = "Location",
      description = "Select the location of the furnace",
      position = 0)
  default Location location() {
    return Location.EDGEVILLE;
  }

  @ConfigItem(
      keyName = "product",
      name = "Product",
      description = "Select the product you'd like to make",
      position = 1)
  default Product product() {
    return Product.MOLTEN_GLASS;
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
