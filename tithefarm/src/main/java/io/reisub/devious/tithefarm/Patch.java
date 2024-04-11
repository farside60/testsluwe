package io.reisub.devious.tithefarm;

import com.google.common.collect.ImmutableList;
import io.reisub.devious.utils.Utils;
import java.util.List;
import lombok.Getter;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.movement.Movement;

@Getter
public class Patch {
  private static List<Patch> patches;
  private static int basePlantId;
  private static int currentIndex;
  private final WorldPoint patchPoint;
  private final WorldPoint interactPoint;

  private Patch(int patchX, int patchY, int interactX, int interactY) {
    patchPoint = Utils.worldToInstance(new WorldPoint(patchX, patchY, 0));
    interactPoint = Utils.worldToInstance(new WorldPoint(interactX, interactY, 0));
  }

  public static void buildList() {
    currentIndex = 0;
    patches =
        ImmutableList.of(
            new Patch(1821, 3483, 1820, 3485),
            new Patch(1816, 3489, 1818, 3489),
            new Patch(1821, 3489, 1819, 3490),
            new Patch(1816, 3492, 1818, 3491),
            new Patch(1821, 3492, 1819, 3493),
            new Patch(1816, 3495, 1818, 3494),
            new Patch(1821, 3495, 1819, 3496),
            new Patch(1816, 3498, 1818, 3497),
            new Patch(1821, 3498, 1819, 3499),
            new Patch(1816, 3504, 1818, 3503),
            new Patch(1821, 3504, 1819, 3505),
            new Patch(1816, 3507, 1818, 3506),
            new Patch(1821, 3507, 1819, 3508),
            new Patch(1816, 3510, 1818, 3509),
            new Patch(1821, 3510, 1819, 3511),
            new Patch(1816, 3513, 1818, 3512),
            new Patch(1821, 3513, 1819, 3514),
            new Patch(1826, 3513, 1824, 3514),
            new Patch(1826, 3510, 1824, 3510),
            new Patch(1826, 3507, 1824, 3508),
            new Patch(1826, 3504, 1824, 3504),
            new Patch(1826, 3498, 1824, 3498),
            new Patch(1826, 3495, 1824, 3496),
            new Patch(1826, 3492, 1824, 3492),
            new Patch(1826, 3489, 1824, 3490));
  }

  public static void setBasePlantId() {
    if (Inventory.contains(ItemID.GOLOVANOVA_SEED)) {
      basePlantId = ObjectID.GOLOVANOVA_PLANT_27393;
    } else if (Inventory.contains(ItemID.BOLOGANO_SEED)) {
      basePlantId = ObjectID.BOLOGANO_PLANT_27404;
    } else if (Inventory.contains(ItemID.LOGAVANO_SEED)) {
      basePlantId = ObjectID.LOGAVANO_PLANT_27415;
    }
  }

  private static void findCurrentIndex() {
    final WorldPoint playerPoint = Players.getLocal().getWorldLocation();

    for (int i = 0; i < patches.size(); i++) {
      if (patches.get(i).getInteractPoint().equals(playerPoint)) {
        currentIndex = i;
        return;
      }
    }

    currentIndex = 0;
  }

  public static Patch getCurrent() {
    if (!patches
        .get(currentIndex)
        .getInteractPoint()
        .equals(Players.getLocal().getWorldLocation())) {
      findCurrentIndex();
    }

    return patches.get(currentIndex);
  }

  public static void takeStep() {
    currentIndex++;

    if (currentIndex == patches.size()) {
      currentIndex = 0;
    }

    do {
      Movement.walk(patches.get(currentIndex).getInteractPoint());
    } while (!Time.sleepTicksUntil(
        () ->
            Players.getLocal()
                .getWorldLocation()
                .equals(patches.get(currentIndex).getInteractPoint()),
        3));
  }

  public static boolean isAtEnd() {
    return currentIndex == patches.size() - 1;
  }

  public TileObject getObject() {
    return TileObjects.getNearest(
        o ->
            o.getActualId() >= ObjectID.TITHE_PATCH
                && o.getActualId() <= ObjectID.LOGAVANO_PLANT_27428
                && o.getWorldLocation().equals(patchPoint));
  }

  public PatchState getState() {
    final int objectId = getObject().getActualId();

    if (objectId == ObjectID.TITHE_PATCH) {
      return PatchState.EMPTY;
    }

    if (Patch.basePlantId == objectId) {
      return PatchState.GROWN;
    }

    switch ((Patch.basePlantId - objectId) % 3) {
      case 0:
        return PatchState.UNWATERED;
      case 2:
        return PatchState.WATERED;
      default:
        return PatchState.DEAD;
    }
  }
}
