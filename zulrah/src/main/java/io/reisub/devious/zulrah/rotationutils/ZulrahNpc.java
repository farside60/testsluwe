package io.reisub.devious.zulrah.rotationutils;

import io.reisub.devious.zulrah.constants.ZulrahLocation;
import io.reisub.devious.zulrah.constants.ZulrahType;
import java.util.Objects;
import javax.annotation.Nonnull;
import lombok.Getter;
import net.runelite.api.NPC;

public final class ZulrahNpc {
  @Nonnull private final ZulrahType type;
  @Nonnull private final ZulrahLocation zulrahLocation;
  @Getter
  private final boolean jad;

  public ZulrahNpc(@Nonnull ZulrahType type, @Nonnull ZulrahLocation zulrahLocation, boolean jad) {
    if (type == null) {
      throw new NullPointerException("type is marked non-null but is null");
    } else {
      this.type = type;
      this.zulrahLocation = zulrahLocation;
      this.jad = jad;
    }
  }

  public static ZulrahNpc valueOf(NPC zulrah, boolean jad) {
    return new ZulrahNpc(
        Objects.requireNonNull(ZulrahType.valueOf(zulrah.getId())),
        Objects.requireNonNull(ZulrahLocation.valueOf(zulrah.getLocalLocation())), jad);
  }

  @Nonnull
  public ZulrahType getType() {
    return type;
  }

  @Nonnull
  public ZulrahLocation getZulrahLocation() {
    return zulrahLocation;
  }

  public String toString() {
    ZulrahType type = getType();
    return "ZulrahNpc(type="
        + type
        + ", zulrahLocation="
        + getZulrahLocation()
        + ", jad="
        + isJad()
        + ")";
  }

  public boolean equals(Object o) {
    if (o == this) {
      return true;
    } else if (!(o instanceof ZulrahNpc)) {
      return false;
    } else {
      ZulrahNpc other = (ZulrahNpc) o;
      Object thisType = getType();
      Object otherType = other.getType();
      if (!thisType.equals(otherType)) {
        return false;
      }

      label29: {
        Object thisZulrahLocation = getZulrahLocation();
        Object otherZulrahLocation = other.getZulrahLocation();
        if (thisZulrahLocation.equals(otherZulrahLocation)) {
          break label29;
        }

        return false;
      }

      return isJad() == other.isJad();
    }
  }

  public int hashCode() {
    int prime = 59;
    int result = 1;
    Object type = getType();
    result = result * prime + type.hashCode();
    Object zulrahLocation = getZulrahLocation();
    result = result * prime + zulrahLocation.hashCode();
    result = result * prime + (isJad() ? 79 : 97);
    return result;
  }
}
