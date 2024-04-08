package io.reisub.sluwe.fletching.tasks;

import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.Activity;
import io.reisub.devious.utils.tasks.Task;
import io.reisub.sluwe.fletching.Config;
import io.reisub.sluwe.fletching.Fletching;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.inject.Inject;
import net.runelite.api.ItemID;
import net.runelite.api.ObjectID;
import net.runelite.api.TileObject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.TileItems;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class Resurface extends Task {
  @Inject private Fletching plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Resurface";
  }

  @Override
  public boolean validate() {
    return Utils.isInRegion(Fletching.FOSSIL_ISLAND_SEAWEED_REGION)
        && plugin.isCurrentActivity(Activity.IDLE)
        && shouldResurface();
  }

  @Override
  public void execute() {
    final TileObject rope = TileObjects.getNearest(ObjectID.ANCHOR_ROPE);
    if (rope == null) {
      return;
    }

    rope.interact("Climb");
    Time.sleepTicksUntil(
        () ->
            Utils.isInRegion(Fletching.FOSSIL_ISLAND_SMALL_ISLAND_REGION)
                || TileItems.getNearest(ItemID.SEAWEED_SPORE) != null,
        30);
  }

  private boolean shouldResurface() {
    switch (config.product()) {
      case ARROW_SHAFTS:
        return !Inventory.contains(ItemID.LOGS);
      case SHORTBOW_U:
      case LONGBOW_U:
        return !Inventory.contains(config.logType().getId());
      case SHORTBOW:
      case LONGBOW:
        return !Inventory.contains(ItemID.BOW_STRING);
      default:
        return getOxygenLevel() <= 20;
    }
  }

  private int getOxygenLevel() {
    final Widget oxygenWidget = Widgets.get(609, 6);
    if (oxygenWidget == null || oxygenWidget.isHidden()) {
      return 100;
    }

    final Pattern regex = Pattern.compile("\\d+");
    final Matcher oxygenMatcher = regex.matcher(oxygenWidget.getText());

    if (oxygenMatcher.find()) {
      return Integer.parseInt(oxygenMatcher.group(0));
    }

    return 0;
  }
}
