package io.reisub.devious.wintertodt;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import net.unethicalite.api.coords.Area;
import net.unethicalite.api.coords.RectangularArea;

@AllArgsConstructor
public class WintertodtProjectile {
  private final int coordX;
  private final int coordY;
  private final boolean aoe;

  @Getter private final Instant start;

  public Area getDamageArea() {
    if (aoe) {
      return new RectangularArea(coordX - 1, coordY - 1, coordX + 1, coordY + 1);
    } else {
      return new RectangularArea(coordX, coordY - 1, coordX + 3, coordY + 2);
    }
  }
}
