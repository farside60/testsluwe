package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.Config;
import io.reisub.devious.tutorialisland.Ironman;
import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.Utils;
import io.reisub.devious.utils.api.SluweMovement;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.DialogOption;
import net.runelite.api.NPC;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.widgets.WidgetInfo;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.magic.Magic;
import net.unethicalite.api.magic.SpellBook;
import net.unethicalite.api.widgets.Dialog;

public class MagicGuide extends Task {
  @Inject private TutorialIsland plugin;
  @Inject private Config config;

  @Override
  public String getStatus() {
    return "Completing Magic Guide";
  }

  @Override
  public boolean validate() {
    if (TutorialIsland.isProgress(670)) {
      if (config.dontLeave()) {
        return false;
      }

      if (config.ironman() != Ironman.NONE && !plugin.isIronman()) {
        return false;
      }
    }

    return TutorialIsland.isProgressBetween(620, 670);
  }

  @Override
  public void execute() {
    if (TutorialIsland.isProgress(620)) {
      SluweMovement.walkTo(new WorldPoint(3140, 3087, 0));
      TutorialIsland.talkTo("Magic Instructor");
    }

    if (TutorialIsland.isProgress(630)) {
      TutorialIsland.openTab(WidgetInfo.RESIZABLE_VIEWPORT_BOTTOM_LINE_MAGIC_TAB);
    }

    if (TutorialIsland.isProgress(640)) {
      TutorialIsland.talkTo("Magic Instructor");
    }

    if (TutorialIsland.isProgress(650)) {
      final NPC chicken = NPCs.getNearest("Chicken");
      if (chicken == null) {
        return;
      }

      Magic.cast(SpellBook.Standard.WIND_STRIKE, chicken);
      TutorialIsland.sleepUntilProgressUpdate(50);
    }

    if (TutorialIsland.isProgress(670)) {
      final NPC instructor = NPCs.getNearest("Magic Instructor");
      if (instructor == null) {
        return;
      }

      instructor.interact("Talk-to");
      Time.sleepTicksUntil(Dialog::isViewingOptions, 30);
      Time.sleepTick();

      Dialog.invokeDialog(DialogOption.CHAT_OPTION_ONE);
      Time.sleepTicksUntil(() -> Utils.isInRegion(12850), 20);
    }
  }
}
