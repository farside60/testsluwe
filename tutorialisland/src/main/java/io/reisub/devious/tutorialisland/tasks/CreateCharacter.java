package io.reisub.devious.tutorialisland.tasks;

import io.reisub.devious.tutorialisland.Config;
import io.reisub.devious.tutorialisland.Gender;
import io.reisub.devious.utils.tasks.Task;
import javax.inject.Inject;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Rand;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.widgets.Widgets;

/** Handle the creation of a new character. */
public class CreateCharacter extends Task {
  @Inject private Config config;

  private final int characterCreationWidgetId = 679;
  private Gender gender;

  @Override
  public String getStatus() {
    return "Creating character";
  }

  @Override
  public boolean validate() {
    return Widgets.isVisible(Widgets.get(characterCreationWidgetId, 1));
  }

  @Override
  public void execute() {
    if (config.gender() == Gender.RANDOM) {
      if (Rand.nextBool()) {
        gender = Gender.MALE;
      } else {
        gender = Gender.FEMALE;
      }
    } else {
      gender = config.gender();
    }

    setGender(gender);

    if (config.randomizeAppearance()) {
      randomizeAppearance();
    }

    final Widget confirmWidget = Widgets.get(characterCreationWidgetId, 68);
    confirmWidget.interact("Confirm");

    Time.sleepTicksUntil(() -> !Widgets.isVisible(Widgets.get(characterCreationWidgetId, 1)), 5);
  }

  private void setGender(Gender gender) {
    final Widget maleWidget = Widgets.get(characterCreationWidgetId, 65);
    final Widget femaleWidget = Widgets.get(characterCreationWidgetId, 66);
    
    switch (gender) {
      case MALE:
        if (maleWidget.hasAction("Male")) {
          maleWidget.interact("Male");
        }
        break;
      case FEMALE:
        if (femaleWidget.hasAction("Female")) {
          femaleWidget.interact("Female");
        }
        break;
      default:
        break;
    }
  }

  private void randomizeAppearance() {
    randomizeDesign();
    randomizeColor();
  }

  private void randomizeDesign() {
    for (int i = 0; i < 7; i++) {
      // skip Jaw on females
      if (gender == Gender.FEMALE && i == 1) {
        continue;
      }

      final int id = Rand.nextBool() ? 12 + (i * 4) : 13 + (i * 4);
      randomizeWidget(Widgets.get(characterCreationWidgetId, id));

      Time.sleepTick();
    }
  }

  private void randomizeColor() {
    for (int i = 0; i < 5; i++) {
      final int id = Rand.nextBool() ? 43 + (i * 4) : 44 + (i * 4);
      randomizeWidget(Widgets.get(characterCreationWidgetId, id));

      Time.sleepTick();
    }
  }

  private void randomizeWidget(Widget widget) {
    final int random = Rand.nextInt(0, 10);

    if (widget.isVisible()) {
      for (int i = 0; i < random; i++) {
        widget.interact("Select");
      }
    }
  }
}
