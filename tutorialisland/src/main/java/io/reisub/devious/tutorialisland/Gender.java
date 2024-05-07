package io.reisub.devious.tutorialisland;

import org.apache.commons.text.WordUtils;

/**
 * Enum with the genders of the character.
 */
public enum Gender {
  MALE,
  FEMALE,
  RANDOM;

  /**
   * Returns the gender as a string.
   *
   * @return the gender as a string
   */
  @Override
  public String toString() {
    return WordUtils.capitalizeFully(name());
  }
}
