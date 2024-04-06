package io.reisub.devious.utils.tasks;

import io.reisub.devious.utils.api.Activity;
import java.util.function.Predicate;
import net.runelite.api.NPC;
import net.runelite.api.TileItem;
import net.runelite.api.TileObject;
import net.unethicalite.api.commons.Predicates;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Reachable;

public abstract class Task {

  public Activity getActivity() {
    return Activity.IDLE;
  }

  public abstract String getStatus();

  public abstract boolean validate();

  public abstract void execute();

  protected boolean npcInteract(int... ids) {
    return npcInteract(0, false, ids);
  }

  protected boolean npcInteract(int actionIndex, int... ids) {
    return npcInteract(actionIndex, false, ids);
  }

  protected boolean npcInteract(int actionIndex, boolean sleepUntilMoving, int... ids) {
    return npcInteract(actionIndex, sleepUntilMoving, Predicates.ids(ids));
  }

  protected boolean npcInteract(int actionIndex, String... names) {
    return npcInteract(actionIndex, false, names);
  }

  protected boolean npcInteract(int actionIndex, boolean sleepUntilMoving, String... names) {
    return npcInteract(actionIndex, sleepUntilMoving, Predicates.names(names));
  }

  protected boolean npcInteract(int actionIndex, Predicate<NPC> filter) {
    return npcInteract(actionIndex, false, filter);
  }

  protected boolean npcInteract(int actionIndex, boolean sleepUntilMoving, Predicate<NPC> filter) {
    return npcInteract(actionIndex, sleepUntilMoving, NPCs.getNearest(filter));
  }

  protected boolean npcInteract(int actionIndex, NPC npc) {
    return npcInteract(actionIndex, false, npc);
  }

  protected boolean npcInteract(String action, int... ids) {
    return npcInteract(action, false, ids);
  }

  protected boolean npcInteract(String action, boolean sleepUntilMoving, int... ids) {
    return npcInteract(action, sleepUntilMoving, Predicates.ids(ids));
  }

  protected boolean npcInteract(String action, String... names) {
    return npcInteract(action, false, names);
  }

  protected boolean npcInteract(String action, boolean sleepUntilMoving, String... names) {
    return npcInteract(action, sleepUntilMoving, Predicates.names(names));
  }

  protected boolean npcInteract(String action, Predicate<NPC> filter) {
    return npcInteract(action, false, filter);
  }

  protected boolean npcInteract(String action, boolean sleepUntilMoving, Predicate<NPC> filter) {
    return npcInteract(action, sleepUntilMoving, NPCs.getNearest(filter));
  }

  protected boolean npcInteract(String action, NPC npc) {
    return npcInteract(action, false, npc);
  }

  protected boolean npcInteract(String action, boolean sleepUntilMoving, NPC npc) {
    if (npc == null || !npc.hasAction(action)) {
      return false;
    }

    return npcInteract(npc.getActionIndex(action), sleepUntilMoving, npc);
  }

  protected boolean npcInteract(int actionIndex, boolean sleepUntilMoving, NPC npc) {
    if (npc == null || npc.getActions().length <= actionIndex) {
      return false;
    }

    npc.interact(actionIndex);

    if (sleepUntilMoving) {
      Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), 3);
    }

    return true;
  }

  protected boolean objectInteract(int id) {
    return objectInteract(0, id);
  }

  protected boolean objectInteract(int actionIndex, int... ids) {
    return objectInteract(actionIndex, false, ids);
  }

  protected boolean objectInteract(int actionIndex, boolean sleepUntilMoving, int... ids) {
    return objectInteract(actionIndex, sleepUntilMoving, Predicates.ids(ids));
  }

  protected boolean objectInteract(int actionIndex, String... names) {
    return objectInteract(actionIndex, false, names);
  }

  protected boolean objectInteract(int actionIndex, boolean sleepUntilMoving, String... names) {
    return objectInteract(actionIndex, sleepUntilMoving, Predicates.names(names));
  }

  protected boolean objectInteract(int actionIndex, Predicate<TileObject> filter) {
    return objectInteract(actionIndex, false, filter);
  }

  protected boolean objectInteract(
      int actionIndex, boolean sleepUntilMoving, Predicate<TileObject> filter) {
    return objectInteract(actionIndex, sleepUntilMoving, TileObjects.getNearest(filter));
  }

  protected boolean objectInteract(String action, int... ids) {
    return objectInteract(action, false, ids);
  }

  protected boolean objectInteract(String action, boolean sleepUntilMoving, int... ids) {
    return objectInteract(action, sleepUntilMoving, Predicates.ids(ids));
  }

  protected boolean objectInteract(String action, String... names) {
    return objectInteract(action, false, names);
  }

  protected boolean objectInteract(String action, boolean sleepUntilMoving, String... names) {
    return objectInteract(action, sleepUntilMoving, Predicates.names(names));
  }

  protected boolean objectInteract(String action, Predicate<TileObject> filter) {
    return objectInteract(action, false, filter);
  }

  protected boolean objectInteract(
      String action, boolean sleepUntilMoving, Predicate<TileObject> filter) {
    return objectInteract(action, sleepUntilMoving, TileObjects.getNearest(filter));
  }

  protected boolean objectInteract(String action, TileObject object) {
    return objectInteract(action, false, object);
  }

  protected boolean objectInteract(String action, boolean sleepUntilMoving, TileObject object) {
    if (object == null || !object.hasAction(action)) {
      return false;
    }

    return objectInteract(object.getActionIndex(action), sleepUntilMoving, object);
  }

  protected boolean objectInteract(int actionIndex, boolean sleepUntilMoving, TileObject object) {
    if (object == null || object.getActions().length <= actionIndex) {
      return false;
    }

    object.interact(actionIndex);

    if (sleepUntilMoving) {
      Time.sleepTicksUntil(() -> Players.getLocal().isMoving(), 3);
    }

    return true;
  }

  protected boolean pickupTileItem(int id) {
    return pickupTileItem(0, id);
  }

  protected boolean pickupTileItem(int sleepTicks, int... ids) {
    return pickupTileItem(sleepTicks, Predicates.ids(ids));
  }

  protected boolean pickupTileItem(String... names) {
    return pickupTileItem(0, names);
  }

  protected boolean pickupTileItem(int sleepTicks, String... names) {
    return pickupTileItem(sleepTicks, names);
  }

  protected boolean pickupTileItem(Predicate<TileItem> filter) {
    return pickupTileItem(0, TileItems.getNearest(filter));
  }

  protected boolean pickupTileItem(int sleepTicks, Predicate<TileItem> filter) {
    return pickupTileItem(sleepTicks, TileItems.getNearest(filter));
  }

  protected boolean pickupTileItem(TileItem item) {
    return pickupTileItem(0, item);
  }

  protected boolean pickupTileItem(int sleepTicks, TileItem item) {
    if (item == null || !Reachable.isInteractable(item)) {
      return false;
    }

    final int count = Inventory.getCount(true, item.getId());

    item.pickup();

    if (sleepTicks == 0) {
      return true;
    }

    return Time.sleepTicksUntil(() -> Inventory.getCount(true, item.getId()) > count, sleepTicks);
  }
}
