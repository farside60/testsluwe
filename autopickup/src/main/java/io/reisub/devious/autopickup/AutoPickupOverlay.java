package io.reisub.devious.autopickup;

import io.reisub.devious.utils.Utils;
import java.awt.BasicStroke;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Polygon;
import javax.inject.Inject;
import net.runelite.api.Perspective;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.OverlayUtil;
import net.unethicalite.client.Static;

public class AutoPickupOverlay extends Overlay {
  private final AutoPickup plugin;

  @Inject
  private AutoPickupOverlay(AutoPickup plugin) {
    this.plugin = plugin;
    setPosition(OverlayPosition.DYNAMIC);
    setLayer(OverlayLayer.ABOVE_SCENE);
    setPriority(PRIORITY_MED);
  }

  @Override
  public Dimension render(Graphics2D graphics2D) {
    for (WorldPoint location : plugin.getLocations()) {
      final LocalPoint localPoint = LocalPoint.fromWorld(Static.getClient(), location);
      renderLocalPoint(graphics2D, localPoint);
    }
    return null;
  }

  private void renderLocalPoint(Graphics2D graphics2D, LocalPoint localPoint) {
    if (localPoint == null) {
      return;
    }

    final Polygon polygon = Perspective.getCanvasTilePoly(Static.getClient(), localPoint);

    if (polygon == null) {
      return;
    }

    OverlayUtil.renderPolygon(graphics2D, polygon, Utils.ARCH_BLUE, new BasicStroke((float) 2));
  }
}
