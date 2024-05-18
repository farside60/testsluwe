package io.reisub.devious.utils.api;

import lombok.RequiredArgsConstructor;
import net.runelite.api.Skill;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.utils.MessageUtils;
import net.unethicalite.api.widgets.Widgets;

@RequiredArgsConstructor
public enum SluweSkill {
  ATTACK(Skill.ATTACK, 2),
  STRENGTH(Skill.STRENGTH, 3),
  RANGED(Skill.RANGED, 4),
  MAGIC(Skill.MAGIC, 5),
  DEFENCE(Skill.DEFENCE, 6),
  HITPOINTS(Skill.HITPOINTS, 7),
  PRAYER(Skill.PRAYER, 8),
  AGILITY(Skill.AGILITY, 9),
  HERBLORE(Skill.HERBLORE, 10),
  THIEVING(Skill.THIEVING, 11),
  CRAFTING(Skill.CRAFTING, 12),
  RUNECRAFT(Skill.RUNECRAFT, 13),
  SLAYER(Skill.SLAYER, 14),
  FARMING(Skill.FARMING, 15),
  MINING(Skill.MINING, 16),
  SMITHING(Skill.SMITHING, 17),
  FISHING(Skill.FISHING, 18),
  COOKING(Skill.COOKING, 19),
  FIREMAKING(Skill.FIREMAKING, 20),
  WOODCUTTING(Skill.WOODCUTTING, 21),
  FLETCHING(Skill.FLETCHING, 22),
  CONSTRUCTION(Skill.CONSTRUCTION, 23),
  HUNTER(Skill.HUNTER, 24);

  private final Skill skill;
  private final int rewardWidgetId;

  public String getName() {
    return skill.getName();
  }

  public Widget getRewardWidget() {
    return Widgets.get(240, rewardWidgetId);
  }

  public void selectRewardWidget() {
    final Widget rewardWidget = getRewardWidget();
    if (rewardWidget == null) {
      return;
    }

    final Widget childWidget = rewardWidget.getChild(9);
    if (childWidget == null) {
      return;
    }

    if (childWidget.getOpacity() == 150) {
      MessageUtils.addMessage(
          "Can't select reward: " + skill.getName() + ". Falling back to Prayer.");
      SluweSkill.PRAYER.selectRewardWidget();
      return;
    }

    rewardWidget.interact(0);
  }
}
