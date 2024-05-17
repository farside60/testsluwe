package io.reisub.devious.autopickup;

import com.google.inject.Provides;
import io.reisub.devious.autopickup.tasks.Burn;
import io.reisub.devious.autopickup.tasks.Fletch;
import io.reisub.devious.autopickup.tasks.GoToSpot;
import io.reisub.devious.autopickup.tasks.HandleBank;
import io.reisub.devious.autopickup.tasks.Hop;
import io.reisub.devious.autopickup.tasks.PickUp;
import io.reisub.devious.autopickup.tasks.PickUpAsh;
import io.reisub.devious.utils.TickScript;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.ConfigList;
import io.reisub.devious.utils.api.SluwePredicates;
import io.reisub.devious.utils.api.SluweWorldPoint;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.ItemID;
import net.runelite.api.Tile;
import net.runelite.api.TileItem;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.PluginDependency;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.client.Static;
import org.pf4j.Extension;
import org.slf4j.Logger;

@Extension
@PluginDependency(Utils.class)
@PluginDescriptor(
    name = "Sluwe Auto Pickup",
    description = "Mom told me not to pick up anything from the ground. So I let the plugin do it.",
    enabledByDefault = false)
@Slf4j
public class AutoPickup extends TickScript {
  @Getter private final List<WorldPoint> locations = new ArrayList<>();
  @Inject private Config config;
  @Getter private ConfigList itemsConfigList;
  @Getter private WorldPoint startLocation;
  @Getter private WorldPoint bankLocation;
  @Getter @Setter private int itemsPickedUp;
  @Getter @Setter private int ashesPickedUp;
  @Getter @Setter private int arrowShaftCount;

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public Logger getLogger() {
    return log;
  }

  @Override
  protected void onStart() {
    reset();

    addTask(Burn.class);
    addTask(Fletch.class);
    addTask(HandleBank.class);
    addTask(GoToSpot.class);
    addTask(PickUp.class);
    addTask(PickUpAsh.class);
    addTask(Hop.class);
  }

  @Override
  protected void onStop() {
    locations.clear();
  }

  private void reset() {
    itemsConfigList = ConfigList.parseList(config.items());
    bankLocation = SluweWorldPoint.parseString(config.bankLocation());
    startLocation = Players.getLocal().getWorldLocation();
    bankLocation = SluweWorldPoint.parseString(config.bankLocation());
    arrowShaftCount = Inventory.getCount(true, ItemID.ARROW_SHAFT);
    setOverlay(getInjector().getInstance(AutoPickupOverlay.class));
  }

  public int getNumberOfItemsRequired() {
    if (config.fletchArrowShafts() && itemsConfigList.getStrings().containsKey("Logs")) {
      return (int) (Math.ceil((double) config.arrowShaftAmount() / 15) + config.amount());
    }

    return config.amount();
  }

  public TileItem getNearestItem() {
    if (locations.isEmpty()) {
      return TileItems.getNearest(SluwePredicates.entityConfigList(itemsConfigList, false, false));
    } else {
      List<TileItem> all =
          TileItems.getAll(SluwePredicates.entityConfigList(itemsConfigList, false, false));

      TileItem nearest = null;

      for (TileItem tileItem : all) {
        if (nearest == null) {
          nearest = tileItem;
          continue;
        }

        if (tileItem.distanceTo(Players.getLocal()) < nearest.distanceTo(Players.getLocal())) {
          nearest = tileItem;
        }
      }

      return nearest;
    }
  }

  public TileItem getNearestAsh() {
    if (locations.isEmpty()) {
      return TileItems.getNearest(ItemID.ASHES);
    } else {
      return TileItems.getNearest(
          i -> {
            if (i.getId() != ItemID.ASHES) {
              return false;
            }

            return locations.contains(i.getWorldLocation());
          });
    }
  }

  public boolean hasFire() {
    if (locations.isEmpty()) {
      return TileObjects.getNearest("Fire") != null;
    } else {
      return TileObjects.getNearest(
              o -> locations.contains(o.getWorldLocation()) && o.getName().equals("Fire"))
          != null;
    }
  }

  @Override
  public void keyPressed(KeyEvent keyEvent) {
    if (config.addLocationHotkey().matches(keyEvent)) {
      keyEvent.consume();
      final Tile tile = Static.getClient().getSelectedSceneTile();
      if (tile == null) {
        return;
      }

      locations.add(new WorldPoint(tile.getWorldX(), tile.getWorldY(), tile.getPlane()));
    }
  }

  @Subscribe
  private void onConfigChanged(ConfigChanged configChanged) {
    if (!configChanged.getGroup().equals("sluweautopickup")) {
      return;
    }

    if (configChanged.getKey().equals("items") && configChanged.getNewValue() != null) {
      itemsConfigList = ConfigList.parseList(configChanged.getNewValue());
    }

    if (configChanged.getKey().equals("bankLocation")) {
      bankLocation = SluweWorldPoint.parseString(config.bankLocation());
    }
  }
}
