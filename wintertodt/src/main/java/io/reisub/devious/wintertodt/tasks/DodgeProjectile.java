package io.reisub.devious.wintertodt.tasks;

import io.reisub.devious.utils.tasks.Task;
import io.reisub.devious.wintertodt.Wintertodt;
import io.reisub.devious.wintertodt.WintertodtProjectile;
import java.time.Instant;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import javax.inject.Inject;
import net.runelite.api.Tile;
import net.runelite.api.coords.WorldPoint;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.movement.Movement;
import net.unethicalite.api.movement.Reachable;
import net.unethicalite.api.scene.Tiles;

public class DodgeProjectile extends Task {
  @Inject public Wintertodt plugin;

  private WintertodtProjectile projectile;

  @Override
  public String getStatus() {
    return "Dodging projectile";
  }

  @Override
  public boolean validate() {
    if (plugin.getProjectiles().isEmpty()) {
      return false;
    }

    Instant now = Instant.now();
    for (Iterator<WintertodtProjectile> it = plugin.getProjectiles().listIterator();
        it.hasNext(); ) {
      WintertodtProjectile p = it.next();

      if (now.isAfter(p.getStart().plusSeconds(4))) {
        it.remove();
        continue;
      }

      if (p.getDamageArea().contains(Players.getLocal())) {
        projectile = p;
        return true;
      }
    }

    return false;
  }

  @Override
  public void execute() {
    WorldPoint safePosition = findSafePosition(Players.getLocal().getWorldLocation());

    if (safePosition != null) {
      Movement.walk(safePosition);
    }

    Time.sleepUntil(() -> Instant.now().isAfter(projectile.getStart().plusSeconds(3)), 10000);
  }

  private WorldPoint findSafePosition(WorldPoint startPosition) {
    // usually south is safe, so we don't need to check lots of tiles
    final WorldPoint south = startPosition.dy(-1);

    if (!projectile.getDamageArea().contains(south)) {
      return south;
    }
    
    List<Tile> safeTiles =
        Tiles.getSurrounding(startPosition, 3).stream()
            .filter(
                tile ->
                    tile != null
                        && !projectile.getDamageArea().contains(tile)
                        && Reachable.isWalkable(tile.getWorldLocation()))
            .collect(Collectors.toUnmodifiableList());

    if (safeTiles.isEmpty()) {
      return null;
    }

    Tile nearest = null;

    for (Tile tile : safeTiles) {
      if (nearest == null) {
        nearest = tile;
        continue;
      }

      if (Players.getLocal().distanceTo(tile) < Players.getLocal().distanceTo(nearest)) {
        nearest = tile;
      }
    }

    return nearest.getWorldLocation();
  }
}
