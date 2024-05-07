package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.Config;
import io.reisub.devious.tutorialisland.Ironman;
import io.reisub.devious.tutorialisland.TutorialIsland;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.DialogOption;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.widgets.Dialog;
import net.unethicalite.api.widgets.Widgets;

public class BecomeIronman extends Task {
  @Inject private TutorialIsland plugin;
  @Inject private Config config;

  private final int ironmanWidgetGroupId = 215;

  @Override
  public String getStatus() {
    return "Become Ironman";
  }

  @Override
  public boolean validate() {
    return config.ironman() != Ironman.NONE
        && !plugin.isIronman()
        && TutorialIsland.isProgress(670);
  }

  @Override
  public void execute() {
    talkWithTutor();
    selectIronmanType();

    Time.sleepTicks(10);
    plugin.setIronman(true);
  }

  private void talkWithTutor() {
    final NPC ironManTutor = NPCs.getNearest("Iron Man tutor");
    if (ironManTutor == null) {
      return;
    }

    ironManTutor.interact(0);
    Time.sleepTicksUntil(Dialog::isViewingOptions, 30);

    Dialog.invokeDialog(DialogOption.CHAT_OPTION_ONE);
    Time.sleepTicks(2);
    Time.sleepTicksUntil(Dialog::isViewingOptions, 30);
    Time.sleepTick();

    Dialog.invokeDialog(DialogOption.CHAT_OPTION_ONE);
    Time.sleepTicksUntil(() -> Widgets.isVisible(Widgets.get(ironmanWidgetGroupId, 1)), 20);
  }


  private void selectIronmanType() {
    switch (config.ironman()) {
      case GROUP:
      case GROUP_HARDCORE:
        final Widget groupModesTab = Widgets.get(ironmanWidgetGroupId, 14, 9);
        if (!Widgets.isVisible(groupModesTab)) {
          return;
        }

        groupModesTab.interact("View");
        Time.sleepTick();
        break;
      default:
        break;
    }

    final Widget typeWidget = Widgets.get(ironmanWidgetGroupId, config.ironman().getWidgetId());
    typeWidget.interact(0);

    Time.sleepTicksUntil(Dialog::isViewingOptions, 10);
  }
}
