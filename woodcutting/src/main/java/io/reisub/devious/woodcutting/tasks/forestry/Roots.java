package io.reisub.devious.woodcutting.tasks.forestry;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.woodcutting.Config;
import io.reisub.devious.woodcutting.Woodcutting;
import javax.inject.Inject;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileObjects;

public class Roots extends Task {
  @Inject private Woodcutting plugin;
  @Inject private Config config;

  private WorldPoint currentPosition = null;

  @Override
  public String getStatus() {
    return "Cutting roots";
  }

  @Override
  public boolean validate() {
    // check if our current root is gone
    if (currentPosition != null
        && TileObjects.getAt(currentPosition, ObjectID.TREE_ROOTS, ObjectID.ANIMAINFUSED_TREE_ROOTS)
            == null) {
      currentPosition = null;
    }

    // check if we're currently cutting a normal root but an anima-infused one is available
    if (currentPosition != null
        && TileObjects.getAt(currentPosition, ObjectID.TREE_ROOTS) != null
        && TileObjects.getNearest(ObjectID.ANIMAINFUSED_TREE_ROOTS) != null) {
      currentPosition = null;
    }

    return config.forestryRoots()
        && currentPosition == null
        && TileObjects.getNearest(ObjectID.TREE_ROOTS, ObjectID.ANIMAINFUSED_TREE_ROOTS) != null;
  }

  @Override
  public void execute() {
    plugin.setActivity(Woodcutting.FORESTRY);

    TileObject roots = TileObjects.getNearest(ObjectID.ANIMAINFUSED_TREE_ROOTS);

    if (roots == null) {
      TileObjects.getNearest(ObjectID.TREE_ROOTS);
    }

    if (roots == null) {
      return;
    }

    roots.interact(0);
    currentPosition = roots.getWorldLocation();
    Time.sleepTick();
  }
}
