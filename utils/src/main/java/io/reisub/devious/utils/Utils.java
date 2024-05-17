package io.reisub.devious.utils;

import com.google.inject.Provides;
import java.awt.Color;
import java.util.Collection;
import javax.inject.Singleton;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.GameState;
import net.runelite.api.Locatable;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.game.Game;
import net.unethicalite.client.Static;
import org.pf4j.Extension;

/**
 * This plugin is required by all 'Sluwe' scripts. It's an extension to the unethicalite API and
 * does nothing by itself.
 */
@Extension
@PluginDescriptor(name = "Sluwe Utils", description = "Utilities for Sluwe scripts")
@Singleton
@Slf4j
public class Utils extends Plugin {
  public static final Color ARCH_BLUE = new Color(23, 147, 208);

  /**
   * Check if the current player is logged in the game.
   *
   * @return true if logged in
   */
  public static boolean isLoggedIn() {
    return Static.getClient() != null && Game.getState() == GameState.LOGGED_IN;
  }

  /**
   * Check if the current player is in any of the given regions.
   *
   * @param regionIds region IDs to check
   * @return true if the current player is in one of the regions
   */
  public static boolean isInRegion(int... regionIds) {
    Player player = Players.getLocal();

    if (player.getWorldLocation() == null) {
      return false;
    }

    for (int regionId : regionIds) {
      if (player.getWorldLocation().getRegionID() == regionId) {
        return true;
      }
    }

    return false;
  }

  /**
   * Same as {@link Utils#isInRegion(int...)} but allows a Collection to be passed.
   *
   * @param regionIds Collection of region IDs to check
   * @see Utils#isInMapRegion(int...)
   */
  public static boolean isInRegion(Collection<Integer> regionIds) {
    return isInRegion(regionIds.stream().mapToInt(i -> i).toArray());
  }

  /**
   * Check if the current player is in any of the given map regions.
   *
   * @param regionIds map region IDs to check
   * @return true if the current player is in one of the map regions
   */
  public static boolean isInMapRegion(int... regionIds) {
    for (int id : Static.getClient().getMapRegions()) {
      for (int regionId : regionIds) {
        if (id == regionId) {
          return true;
        }
      }
    }

    return false;
  }

  /**
   * Same as {@link Utils#isInMapRegion(int...)} but allows a Collection to be passed.
   *
   * @param regionIds Collection of map region IDs to check
   * @see Utils#isInMapRegion(int...)
   */
  public static boolean isInMapRegion(Collection<Integer> regionIds) {
    return isInMapRegion(regionIds.stream().mapToInt(i -> i).toArray());
  }

  /**
   * Translate a world point from a locatable entity in an instance to its world version.
   *
   * @param locatable the locatable entity in an instance
   * @return the world point of the entity as it is known outside an instanced zone
   */
  public static WorldPoint instanceToWorld(Locatable locatable) {
    return WorldPoint.fromLocalInstance(Static.getClient(), locatable.getLocalLocation());
  }

  /**
   * Translate a normal world point to the point in the current instance.
   *
   * @param worldPoint the world point as it is known outside an instanced zone
   * @return the world point in the instance corresponding to the given normal world point
   */
  public static WorldPoint worldToInstance(WorldPoint worldPoint) {
    final Collection<WorldPoint> points =
        WorldPoint.toLocalInstance(Static.getClient(), worldPoint);

    if (points.isEmpty()) {
      return null;
    }

    return points.iterator().next();
  }

  @Provides
  public Config getConfig(ConfigManager configManager) {
    return configManager.getConfig(Config.class);
  }

  @Override
  public void startUp() {
    log.info(this.getName() + " started");
  }

  @Override
  protected void shutDown() {
    log.info(this.getName() + " stopped");
  }
}
