package com.eronalves.patternmatching;

/**
 * Match
 */
public class Match<T> {

  static public <T> Match<T> a(T value) {
    return new Match<>(value);
  }

  private T value;

  private Match(T value) {
    this.value = value;
  }

}
