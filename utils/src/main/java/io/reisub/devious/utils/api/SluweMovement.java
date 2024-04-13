package io.reisub.devious.utils.api;

import com.google.common.collect.Sets;
import io.reisub.devious.utils.Constants;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.enums.FairyRingCode;
import io.reisub.devious.utils.enums.HouseTeleport;
import io.reisub.devious.utils.enums.HouseTeleport.TeleportItem;
import java.lang.reflect.Field;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.EquipmentInventorySlot;
import net.runelite.api.GameState;
import net.runelite.api.Item;
import net.runelite.api.ItemID;
import net.runelite.api.Locatable;
import net.runelite.api.MenuAction;
import net.runelite.api.ObjectID;
import net.runelite.api.Player;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldArea;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.coords.RectangularArea;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.game.Combat;
import net.unethicalite.api.game.Game;
import net.unethicalite.api.game.GameThread;
import net.unethicalite.api.items.Equipment;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.magic.SpellBook.Standard;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.movement.pathfinder.Walker;
import net.unethicalite.api.movement.pathfinder.model.Transport;
import net.unethicalite.api.widgets.Widgets;
import net.unethicalite.client.Static;

@Slf4j
public class SluweMovement {

  private static final int DEFAULT_TIMEOUT = 100;
  private static final int DESTINATION_DISTANCE = 8;

  public static boolean interrupted;

  public static void walk(WorldPoint destination, final int rand) {
    walk(destination, rand, rand);
  }

  public static void walk(WorldPoint destination, final int x, final int y) {
    destination = destination.dx(Rand.nextInt(-x, x + 1)).dy(Rand.nextInt(-y, y + 1));

    Movement.walk(destination);
  }

  public static void walkTo(WorldPoint destination) {
    walkTo(destination, 0, false);
  }

  public static void walkTo(WorldPoint destination, boolean disableTeleports) {
    walkTo(destination, 0, null, DEFAULT_TIMEOUT, DESTINATION_DISTANCE, disableTeleports);
  }

  public static void walkTo(WorldPoint destination, Runnable task) {
    walkTo(destination, 0, task, false);
  }

  public static void walkTo(WorldPoint destination, Runnable task, boolean disableTeleports) {
    walkTo(destination, 0, task, disableTeleports);
  }

  public static void walkTo(WorldPoint destination, int radius) {
    walkTo(destination, radius, null, false);
  }

  public static void walkTo(WorldPoint destination, int radius, boolean disableTeleports) {
    walkTo(destination, radius, null, DEFAULT_TIMEOUT, disableTeleports);
  }

  public static void walkTo(WorldPoint destination, int radius, Runnable task) {
    walkTo(destination, radius, task, false);
  }

  public static void walkTo(
      WorldPoint destination, int radius, Runnable task, boolean disableTeleports) {
    walkTo(destination, radius, task, DEFAULT_TIMEOUT, disableTeleports);
  }

  public static void walkTo(WorldPoint destination, int radius, Runnable task, int tickTimeout) {
    walkTo(destination, radius, task, tickTimeout, false);
  }

  public static void walkTo(
      WorldPoint destination,
      int radius,
      Runnable task,
      int tickTimeout,
      boolean disableTeleports) {
    walkTo(destination, radius, task, tickTimeout, DESTINATION_DISTANCE, disableTeleports);
  }

  public static void walkTo(
      WorldPoint destination, int radius, Runnable task, int tickTimeout, int destinationDistance) {
    walkTo(destination, radius, task, tickTimeout, destinationDistance, false);
  }

  public static void walkTo(
      WorldPoint destination,
      int radius,
      Runnable task,
      int tickTimeout,
      int destinationDistance,
      boolean disableTeleports) {
    int start = Static.getClient().getTickCount();

    if (radius > 0) {
      destination =
          destination.dx(Rand.nextInt(-radius, radius + 1)).dy(Rand.nextInt(-radius, radius + 1));
    }

    do {
      if (!Movement.isWalking() && Static.getClient().getGameState() != GameState.LOADING) {
        if (disableTeleports) {
          walkToWithoutTeleports(destination);
        } else {
          Movement.walkTo(destination);
        }

        if (!Players.getLocal().isMoving()) {
          Time.sleepTick();
        }
      } else if (task != null) {
        Static.getClientThread().invoke(task);
      }

      Time.sleepTick();
    } while (!interrupted
        && Players.getLocal().distanceTo(destination) > destinationDistance
        && Static.getClient().getTickCount() <= start + tickTimeout
        && (Static.getClient().getGameState() == GameState.LOADING
            || Static.getClient().getGameState() == GameState.LOGGED_IN));

    interrupted = false;
  }

  public static boolean walkToWithoutTeleports(WorldPoint destination) {
    return walkToWithoutTeleports(destination.toWorldArea());
  }

  public static boolean walkToWithoutTeleports(WorldArea destination) {
    Player local = Players.getLocal();
    if (destination.contains(local)) {
      try {
        Field field = Walker.class.getDeclaredField("currentDestination");
        field.setAccessible(true);
        field.set(null, null);
      } catch (NoSuchFieldException | IllegalAccessException exception) {
        log.warn("Failed to set currentDestination in class Walker to null");
      }

      return true;
    }

    if (Game.isInCutscene() || Widgets.isVisible(Widgets.get(299, 0))) {
      Time.sleepTicks(2);
      return false;
    }

    final Map<WorldPoint, List<Transport>> transports = Walker.buildTransportLinks();
    List<WorldPoint> path = Walker.buildPath(destination);

    Static.getEntityRenderer().setCurrentPath(path);

    if (path == null || path.isEmpty()) {
      return false;
    }

    WorldPoint localWorldPoint = local.getWorldLocation();
    boolean offPath =
        path.stream()
            .noneMatch(
                t -> t.distanceTo(localWorldPoint) <= 5 && Walker.canPathTo(localWorldPoint, t));

    if (offPath) {
      path = Walker.buildPath(destination, true);
    }

    return Walker.walkAlong(path, transports);
  }

  public static boolean openDoor(Locatable target) {
    return openDoor(target, Sets.newHashSet());
  }

  public static boolean openDoor(Locatable target, final int maxDistance) {
    return openDoor(target, maxDistance, Sets.newHashSet());
  }

  public static boolean openDoor(Locatable target, final Set<WorldPoint> ignoreLocations) {
    return openDoor(target, Integer.MAX_VALUE, ignoreLocations);
  }

  public static boolean openDoor(
      Locatable target, final int maxDistance, final Set<WorldPoint> ignoreLocations) {
    if (target == null) {
      return false;
    }

    final WorldPoint targetLocation = target.getWorldLocation();

    final TileObject door =
        TileObjects.getNearest(
            targetLocation,
            o ->
                o.getName().equals("Door")
                    && o.hasAction("Open")
                    && o.distanceTo(targetLocation) <= maxDistance
                    && !ignoreLocations.contains(o.getWorldLocation()));

    if (door == null) {
      return false;
    }

    if (!Reachable.isInteractable(door)) {
      ignoreLocations.add(door.getWorldLocation());

      if (!openDoor(door, maxDistance, ignoreLocations)) {
        return false;
      }
    }

    GameThread.invoke(() -> door.interact("Open"));

    final WorldPoint tile = door.getWorldLocation();

    return Time.sleepTicksUntil(
        () -> TileObjects.getFirstAt(tile, o -> o.hasAction("Open")) == null, 15);
  }

  public static boolean teleportToHouse() {
    if (Inventory.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS))
        || Equipment.contains(Predicates.ids(Constants.CONSTRUCTION_CAPE_IDS))) {
      Interact.interactWithInventoryOrEquipment(
          Constants.CONSTRUCTION_CAPE_IDS, "Tele to POH", null, -1);
    } else if (Standard.TELEPORT_TO_HOUSE.canCast()) {
      Standard.TELEPORT_TO_HOUSE.cast();
    } else if (Inventory.contains(ItemID.TELEPORT_TO_HOUSE)) {
      Inventory.getFirst(ItemID.TELEPORT_TO_HOUSE).interact("Break");
    } else {
      return false;
    }

    return Time.sleepTicksUntil(
        () ->
            Static.getClient().isInInstancedRegion()
                && TileObjects.getNearest(ObjectID.PORTAL_4525) != null,
        10);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport) {
    return teleportThroughHouse(houseTeleport, false);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport, int energyThreshold) {
    return teleportThroughHouse(houseTeleport, false, energyThreshold);
  }

  public static boolean teleportThroughHouse(HouseTeleport houseTeleport, boolean forceNexus) {
    return teleportThroughHouse(houseTeleport, forceNexus, 30);
  }

  public static boolean teleportThroughHouse(
      HouseTeleport houseTeleport, boolean forceNexus, int energyThreshold) {
    if (!Static.getClient().isInInstancedRegion()) {
      if (!teleportToHouse()) {
        return false;
      }
      Time.sleepTicks(2);
    }

    drinkFromPool(energyThreshold);

    if (forceNexus) {
      return teleportThroughPortalNexus(houseTeleport);
    }

    if (houseTeleport.getItem() == null) {
      if (TileObjects.getNearest(houseTeleport.getPortalId()) != null) {
        return teleportThroughPortal(houseTeleport);
      } else {
        return teleportThroughPortalNexus(houseTeleport);
      }
    } else {
      return teleportThroughItem(houseTeleport);
    }
  }

  public static void drinkFromPool(final int energyThreshold) {
    if (Movement.getRunEnergy() < energyThreshold) {
      final TileObject pool =
          TileObjects.getNearest(Predicates.ids(Constants.REJUVENATION_POOL_IDS));

      if (pool != null) {
        GameThread.invoke(() -> pool.interact(0));

        Time.sleepTicksUntil(
            () -> Movement.getRunEnergy() == 100 && Combat.getMissingHealth() == 0, 10);
        Time.sleepTick();

        if (!Movement.isRunEnabled()) {
          Movement.toggleRun();
        }
      }
    }
  }

  public static boolean teleportThroughPortal(HouseTeleport houseTeleport) {
    final TileObject portal = TileObjects.getNearest(houseTeleport.getPortalId());

    GameThread.invoke(() -> portal.interact("Enter"));

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
  }

  public static boolean teleportThroughPortalNexus(HouseTeleport houseTeleport) {
    final TileObject portalNexus =
        TileObjects.getNearest(Predicates.ids(Constants.PORTAL_NEXUS_IDS));

    if (portalNexus == null) {
      return false;
    }

    final String destination = houseTeleport.getName().toLowerCase();

    for (String action : portalNexus.getActions()) {
      if (action != null && action.toLowerCase().contains(destination)) {
        GameThread.invoke(() -> portalNexus.interact(action));

        return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
      }
    }

    GameThread.invoke(() -> portalNexus.interact("Teleport Menu"));

    if (!Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(17, 2)), 30)) {
      return false;
    }

    final Widget[] children = Widgets.get(17, 12).getChildren();

    if (children == null) {
      return false;
    }

    int i;

    for (i = 0; i < children.length; i++) {
      if (children[i].getText().toLowerCase().contains(destination)) {
        break;
      }
    }

    final Widget destinationWidget = Widgets.get(17, 13, i);

    if (destinationWidget == null) {
      return false;
    }

    destinationWidget.interact(
        0,
        MenuAction.WIDGET_CONTINUE.getId(),
        destinationWidget.getIndex(),
        destinationWidget.getId());

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 10);
  }

  public static boolean teleportThroughItem(HouseTeleport houseTeleport) {
    final TileObject item = TileObjects.getNearest(houseTeleport.getItem().getNames());

    if (item == null) {
      return false;
    }

    final String destination = houseTeleport.getName().toLowerCase();

    for (String action : item.getActions()) {
      if (action != null && action.toLowerCase().contains(destination)) {
        GameThread.invoke(() -> item.interact(action));

        return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 30);
      }
    }

    GameThread.invoke(() -> item.interact(houseTeleport.getItem().getAction()));

    final int widgetGroupId = houseTeleport.getWidgetGroupId();
    final int widgetId = houseTeleport.getWidgetId();

    if (!Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(widgetGroupId, widgetId)), 30)) {
      return false;
    }

    final Widget[] children = Widgets.get(widgetGroupId, widgetId).getChildren();

    if (children == null) {
      return false;
    }

    int childId;

    for (childId = 0; childId < children.length; childId++) {
      if (children[childId].getText().toLowerCase().contains(destination)) {
        break;
      }
    }

    final Widget destinationWidget = Widgets.get(widgetGroupId, widgetId, childId);

    if (destinationWidget == null) {
      return false;
    }

    if (houseTeleport.getItem() == TeleportItem.DIGSITE_PENDANT) {
      destinationWidget.interact(
          0,
          MenuAction.WIDGET_CONTINUE.getId(),
          destinationWidget.getIndex(),
          destinationWidget.getId());
    } else {
      destinationWidget.interact(
          1, MenuAction.CC_OP.getId(), destinationWidget.getIndex(), destinationWidget.getId());
    }

    return Time.sleepTicksUntil(() -> !Static.getClient().isInInstancedRegion(), 10);
  }

  public static boolean useFairyRing(FairyRingCode fairyRingCode) {
    return useFairyRing(fairyRingCode, 0, 0);
  }

  public static boolean useFairyRing(FairyRingCode fairyRingCode, int xoffset, int yoffset) {
    if (!Movement.isRunEnabled()) {
      Movement.toggleRun();
    }

    if (!goToFairyRing()) {
      return false;
    }

    final TileObject fairyRing = TileObjects.getNearest("Fairy ring");
    final String lastDestinationAction =
        String.format("Last-destination (%s)", fairyRingCode.getCode());
    final int regionId = fairyRing.getWorldLocation().getRegionID();

    final Item staff = Inventory.getFirst(ItemID.LUNAR_STAFF, ItemID.DRAMEN_STAFF);
    final Item originalWeapon = Equipment.fromSlot(EquipmentInventorySlot.WEAPON);

    if (staff != null) {
      staff.interact("Wield");
    }

    if (fairyRing.hasAction(lastDestinationAction)) {
      fairyRing.interact(lastDestinationAction);
      Time.sleepTicksUntil(() -> !Players.getLocal().isMoving(), 50);
    } else {
      fairyRing.interact("Configure");
      Time.sleepTicksUntil(() -> !Players.getLocal().isMoving(), 50);

      if (!Time.sleepTicksUntil(
          () -> {
            final Widget fairyRingFavoritesWidget = Widgets.get(381, 7);
            return fairyRingFavoritesWidget != null && fairyRingFavoritesWidget.isVisible();
          },
          10)) {
        return false;
      }

      fairyRingCode
          .getWidget()
          .interact(1, MenuAction.CC_OP.getId(), -1, fairyRingCode.getWidget().getId());
      Time.sleepTicksUntil(
          () -> {
            final Widget fairyRingTeleportButton =
                Widgets.get(WidgetInfo.FAIRY_RING_TELEPORT_BUTTON);
            return fairyRingTeleportButton != null
                && fairyRingTeleportButton.isVisible()
                && !fairyRingTeleportButton.getText().equals("Invalid location");
          },
          10);

      final Widget fairyRingTeleportButton = Widgets.get(WidgetInfo.FAIRY_RING_TELEPORT_BUTTON);
      fairyRingTeleportButton.interact(
          1, MenuAction.CC_OP.getId(), -1, fairyRingTeleportButton.getId());
    }

    if (!Time.sleepTicksUntil(
        () -> Players.getLocal().getWorldLocation().getRegionID() != regionId, 10)) {
      return false;
    }

    Time.sleepTick();

    // we walk one tile away from the fairy ring because pathfinding doesn't seem to work
    // when we're standing on a fairy ring
    if (xoffset == 0 && yoffset == 0) {
      xoffset = fairyRingCode.getDefaultXoffset();
      yoffset = fairyRingCode.getDefaultYoffset();
    }

    Movement.walk(Players.getLocal().getWorldLocation().dx(xoffset).dy(yoffset));

    if (originalWeapon != null && staff != null) {
      final Item originalWeaponInventory = Inventory.getFirst(originalWeapon.getId());
      if (originalWeaponInventory != null) {
        originalWeaponInventory.interact("Wield");
      }
    }

    Time.sleepTick();

    return true;
  }

  public static boolean goToFairyRing() {
    if (TileObjects.getNearest("Fairy ring") != null) {
      return true;
    }

    final Item ardougneCloak = Inventory.getFirst(Predicates.ids(Constants.ARDOUGNE_CLOAK_IDS));
    if (ardougneCloak != null) {
      ardougneCloak.interact("Monastery Teleport");
      Time.sleepTicksUntil(() -> Utils.isInRegion(10290), 10);
      Time.sleepTick();

      SluweMovement.walkTo(new WorldPoint(2655, 3229, 0));
    }

    return TileObjects.getNearest("Fairy ring") != null;
  }

  public static WorldPoint toInstance(final WorldPoint point) {
    final Collection<WorldPoint> instancePoints =
        WorldPoint.toLocalInstance(Static.getClient(), point);

    return instancePoints.stream().findFirst().orElse(null);
  }

  public static RectangularArea toInstance(final RectangularArea area) {
    final WorldPoint sw =
        toInstance(new WorldPoint(area.getMinX(), area.getMinY(), area.getPlane()));
    final WorldPoint ne =
        toInstance(new WorldPoint(area.getMaxX(), area.getMaxY(), area.getPlane()));

    if (sw == null || ne == null) {
      return null;
    }

    return new RectangularArea(sw, ne);
  }

  public static boolean isInteractable(Locatable locatable) {
    return getInteractable(locatable).stream().anyMatch(Reachable::isWalkable);
  }

  public static List<WorldPoint> getInteractable(Locatable locatable) {
    WorldArea locatableArea = locatable.getWorldArea();
    WorldArea surrounding = locatableArea.offset(1);

    // List of tiles that can interact with worldArea and can be walked on
    return surrounding.toWorldPointList().stream()
        .filter(p -> !locatableArea.contains(p))
        .filter(p -> !Reachable.isObstacle(p))
        .collect(Collectors.toList());
  }
}
