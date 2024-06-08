package com.eronalves.patternmatching;

import java.util.List;

/**
 * Match
 */
public class Match<T, R> {

  static public <T, R> Match<T, R> a(T value) {
    return new Match<>(value);
  }

  private T value;
  private List<Pattern<T, R>> cases;

  private Match(T value) {
    this.value = value;
  }

  public void addCase(Pattern<T, R> aCase) {
    this.cases.add(aCase);
  }

  public R run() {
    for (Pattern<T, R> aCase : cases) {
      if (aCase.test(this.value)) {
        return aCase.apply(this.value);
      }
    }
    throw new IllegalStateException("Unsuccesfull match");
  }

}
