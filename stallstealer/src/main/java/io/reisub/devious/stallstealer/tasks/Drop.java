package io.reisub.devious.stallstealer.tasks;

import io.reisub.devious.stallstealer.Config;
import io.reisub.devious.utils.api.ConfigList;
import io.reisub.devious.utils.tasks.Task;
import java.util.Set;
import javax.inject.Inject;
import net.runelite.api.Item;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.unethicalite.api.items.Inventory;

public class Drop extends Task {
  private final Config config;
  private String[] dropItems;

  @Inject
  public Drop(Config config) {
    this.config = config;
    dropItems = parseDropItems();
  }

  @Override
  public String getStatus() {
    return "Dropping";
  }

  @Override
  public boolean validate() {
    return Inventory.contains(dropItems);
  }

  @Override
  public void execute() {
    final Item item = Inventory.getFirst(dropItems);
    if (item == null) {
      return;
    }

    item.interact("Drop");
  }

  @Subscribe
  private void onConfigChanged(ConfigChanged event) {
    if (!event.getGroup().equals("sluwestallstealer")) {
      return;
    }

    if (event.getKey().equals("dropItems")) {
      dropItems = parseDropItems();
    }
  }

  private String[] parseDropItems() {
    Set<String> keys = ConfigList.parseList(config.dropItems()).getStrings().keySet();

    return keys.toArray(new String[0]);
  }
}
