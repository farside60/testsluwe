package io.reisub.devious.wintertodt;

import javax.annotation.concurrent.Immutable;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Immutable
public class BossData {
  @Getter private final int health;

  @Getter private final int world;

  @Getter private final long time;

  @Getter private final int timer;
}
