package io.reisub.devious.tithefarm;

import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.widgets.Widgets;

@Getter
@RequiredArgsConstructor
public enum Reward {
  GRICOLLERS_CAN(200, () -> Widgets.get(236, 9)),
  AUTO_WEED(50, () -> Widgets.get(236, 15)),
  FARMERS_TORSO(150, () -> Widgets.get(236, 26)),
  FARMERS_LEGS(125, () -> Widgets.get(236, 30)),
  FARMERS_HAT(75, () -> Widgets.get(236, 34)),
  FARMERS_BOOTS(50, () -> Widgets.get(236, 9)),
  HERB_SACK(250, () -> Widgets.get(236, 14)),
  SEED_BOX(250, () -> Widgets.get(236, 10)),
  HERB_BOX(30, () -> Widgets.get(236, 7, 4)),
  GRAPE_SEED(2, () -> Widgets.get(236, 7, 2));

  private final int cost;
  private final Supplier<Widget> widget;
}
