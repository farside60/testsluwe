package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import java.util.function.Supplier;
import net.runelite.api.TileObject;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.Players;
import net.unethicalite.api.entities.TileObjects;
import net.unethicalite.api.widgets.Widgets;

public class MiningGuide extends Task {
  private final Supplier<Widget> daggerWidget = () -> Widgets.get(312, 9);

  @Override
  public String getStatus() {
    return "Completing Mining Guide";
  }

  @Override
  public boolean validate() {
    return TutorialIsland.isProgressBetween(260, 360);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(260)) {
      talkToGuide();
    }

    if (TutorialIsland.isProgress(300)) {
      TutorialIsland.interactObject("Tin rocks", "Mine");
    }

    if (TutorialIsland.isProgress(310)) {
      TutorialIsland.interactObject("Copper rocks", "Mine");
    }

    if (TutorialIsland.isProgress(320)) {
      TutorialIsland.useItemOnObject("Tin ore", "Furnace");
    }

    if (TutorialIsland.isProgress(330)) {
      TutorialIsland.talkTo("Mining Instructor");
    }

    if (TutorialIsland.isProgressBetween(340, 350)) {
      smith();
    }

    if (TutorialIsland.isProgress(360)) {
      TutorialIsland.open("Gate");
    }
  }

  private void talkToGuide() {
    final WorldPoint destination = new WorldPoint(3080, 9505, 0);
    if (Players.getLocal().distanceTo(destination) > 5) {
      SluweMovement.walkTo(destination, 3);
    }
    TutorialIsland.talkTo("Mining Instructor");

    // progress 270 is skipped automatically, I assume it's the old prospect rock step
    // we should wait a bit until we're at 300
    Time.sleepTicksUntil(() -> TutorialIsland.isProgress(300), 5);
  }

  private void smith() {
    if (!Widgets.isVisible(daggerWidget.get())) {
      final TileObject anvil = TileObjects.getNearest("Anvil");
      if (anvil == null) {
        return;
      }

      anvil.interact("Smith");
      Time.sleepTicksUntil(() -> Widgets.isVisible(daggerWidget.get()), 20);
    }

    daggerWidget.get().interact("Smith");
    TutorialIsland.sleepUntilProgressUpdate(10);
  }
}
