package io.reisub.devious.tithefarm.tasks;

import io.reisub.devious.utils.tasks.Task;
import java.util.function.Supplier;
import net.runelite.api.ItemID;
import net.runelite.api.NPC;
import net.runelite.api.widgets.Widget;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.entities.NPCs;
import net.unethicalite.api.items.Inventory;
import net.unethicalite.api.widgets.Widgets;

public class GetTools extends Task {
  private static final int TOOL_WIDGET_ID = 125;
  private static final Supplier<Widget> TOOLS = () -> Widgets.get(TOOL_WIDGET_ID, 0);
  private static final Supplier<Widget> CLOSE = () -> Widgets.get(TOOL_WIDGET_ID, 1, 11);
  private static final Supplier<Widget> DIBBER = () -> Widgets.get(TOOL_WIDGET_ID, 9);
  private static final Supplier<Widget> SPADE = () -> Widgets.get(TOOL_WIDGET_ID, 10);

  @Override
  public String getStatus() {
    return "Taking seed dibber";
  }

  @Override
  public boolean validate() {
    return !Inventory.contains(ItemID.SEED_DIBBER) || !Inventory.contains(ItemID.SPADE);
  }

  @Override
  public void execute() {
    final NPC leprechaun = NPCs.getNearest("Tool Leprechaun");
    if (leprechaun == null) {
      return;
    }

    leprechaun.interact("Exchange");
    Time.sleepTicksUntil(() -> Widgets.isVisible(TOOLS.get()), 10);
    Time.sleepTick();

    if (!Inventory.contains(ItemID.SEED_DIBBER)) {
      DIBBER.get().interact("Remove-1");
    }

    if (!Inventory.contains(ItemID.SPADE)) {
      SPADE.get().interact("Remove-1");
    }

    CLOSE.get().interact("Close");
    Time.sleepTicksUntil(
        () -> Inventory.contains(ItemID.SEED_DIBBER) && Inventory.contains(ItemID.SPADE), 5);
  }
}
