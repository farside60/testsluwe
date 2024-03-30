package io.reisub.devious.blackjack;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import net.runelite.api.NpcID;

@Getter
@RequiredArgsConstructor
public enum Target {
  BEARDED_BANDIT(NpcID.BANDIT_737, Room.NORTH),
  BANDIT(NpcID.BANDIT_735, Room.NORTH),
  MENAPHITE_THUG(NpcID.MENAPHITE_THUG, Room.SOUTH);

  private final int id;
  private final Room room;
}
