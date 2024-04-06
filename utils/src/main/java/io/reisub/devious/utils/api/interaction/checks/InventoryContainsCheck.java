package io.reisub.devious.utils.api.interaction.checks;

import java.util.Arrays;
import net.unethicalite.api.commons.Time;
import net.unethicalite.api.items.Inventory;

public class InventoryContainsCheck extends Check {
  private final int[] ids;
  private final String[] names;

  public InventoryContainsCheck(int timeout, int... ids) {
    this(timeout, false, ids);
  }

  public InventoryContainsCheck(int timeout, boolean ignoreFailure, int... ids) {
    super(timeout, ignoreFailure);
    this.ids = ids;
    this.names = null;
  }

  public InventoryContainsCheck(int timeout, String... names) {
    this(timeout, false, names);
  }

  public InventoryContainsCheck(int timeout, boolean ignoreFailure, String... names) {
    super(timeout, ignoreFailure);
    this.ids = null;
    this.names = names;
  }

  @Override
  public void check() throws CheckFailedException {
    if (ids == null && names == null) {
      return;
    }

    if (ids != null) {
      if (!Time.sleepTicksUntil(() -> Inventory.contains(ids), getTimeout())) {
        final String idsString = Arrays.toString(ids);

        throw new CheckFailedException(
            String.format("Timed out waiting for inventory to contain ids '%s'", idsString),
            getTimeout());
      }
    } else {
      if (!Time.sleepTicksUntil(() -> Inventory.contains(names), getTimeout())) {
        final String namesString = Arrays.toString(names);

        throw new CheckFailedException(
            String.format("Timed out waiting for inventory to contain '%s'", namesString),
            getTimeout());
      }
    }
  }
}
