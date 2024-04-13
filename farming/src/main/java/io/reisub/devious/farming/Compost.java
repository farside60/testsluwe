package io.reisub.devious.farming;

import io.reisub.devious.utils.Constants;
import java.util.function.Supplier;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.ItemID;
import net.runelite.api.widgets.Widget;

@RequiredArgsConstructor
public enum Compost {
  NORMAL(ItemID.COMPOST, Constants.TOOLS_WITHDRAW_COMPOST_WIDGET),
  SUPER(ItemID.SUPERCOMPOST, Constants.TOOLS_WITHDRAW_SUPERCOMPOST_WIDGET),
  ULTRA(ItemID.ULTRACOMPOST, Constants.TOOLS_WITHDRAW_ULTRACOMPOST_WIDGET),
  BOTTOMLESS(ItemID.BOTTOMLESS_COMPOST_BUCKET, Constants.TOOLS_WITHDRAW_BOTTOMLESS_BUCKET_WIDGET);

  @Getter private final int id;
  private final Supplier<Widget> widgetSupplier;

  public Widget getWidget() {
    return widgetSupplier.get();
  }
}
