package com.eronalves.patternmatching;

import java.util.ArrayList;
import java.util.List;

/**
 * Match
 */
public class Match<T, R> {

  static public <T, R> Match<T, R> a(T value) {
    return new Match<>(value);
  }

  private T value;
  private List<Pattern<?, R>> cases;

  private Match(T value) {
    this.value = value;
    this.cases = new ArrayList<>();
  }

  public Match<T, R> addCase(Pattern<?, R> aCase) {
    this.cases.add(aCase);
    return this;
  }

  public R run() {
    for (Pattern<?, R> aCase : cases) {
      if (aCase.test(this.value)) {
        return aCase.apply(this.value);
      }
    }
    throw new IllegalStateException("Unsuccesfull match");
  }

}
