package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import net.runelite.api.Item;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class CombatGuide extends Task {
  @Override
  public String getStatus() {
    return "Completing Combat Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(370, 480);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(370)) {
      TutorialIsland.talkTo("Combat Instructor");
    }

    if (TutorialIsland.isProgress(390)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_EQUIP_TAB);
    }

    if (TutorialIsland.isProgress(400)) {
      TutorialIsland.interactWidget(387, 1);
    }

    if (TutorialIsland.isProgress(405)) {
      wield(true, "Bronze dagger");
    }

    if (TutorialIsland.isProgress(410)) {
      TutorialIsland.talkTo("Combat Instructor");
    }

    if (TutorialIsland.isProgress(420)) {
      wield(true, "Bronze sword", "Wooden shield");
    }

    if (TutorialIsland.isProgress(430)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_COMBAT_TAB);
    }

    if (TutorialIsland.isProgress(440)) {
      TutorialIsland.open("Gate", new WorldPoint(3111, 9518, 0));
    }

    if (TutorialIsland.isProgress(450)) {
      TutorialIsland.interactNpc("Giant rat", "Attack");

      // wait until the rat is dead
      Time.sleepTicksUntil(() -> TutorialIsland.isProgress(470), 50);
    }

    if (TutorialIsland.isProgress(470)) {
      talkToVannaka();
    }

    if (TutorialIsland.isProgress(480)) {
      wield(false, "Shortbow", "Bronze arrow");
      TutorialIsland.interactNpc("Giant rat", "Attack");

      // wait until the rat is dead
      Time.sleepTicksUntil(() -> TutorialIsland.isProgress(500), 50);
    }
  }

  private void wield(boolean wait, String... names) {
    for (String name : names) {
      final Item item = Inventory.getFirst(name);
      if (item == null) {
        return;
      }

      item.interact("Wield");
    }

    if (wait) {
      TutorialIsland.sleepUntilProgressUpdate(5);
    }
  }

  private void talkToVannaka() {
    SluweMovement.walkTo(new WorldPoint(3111, 9514, 0));

    TutorialIsland.talkTo("Combat Instructor");
  }
}
