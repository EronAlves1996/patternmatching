package com.eronalves.patternmatching;

import java.util.ArrayList;
import java.util.List;

/**
 * Match
 */
public class Match<R> {

  public static MatchTypeDefinition a(Object value) {
    return new MatchTypeDefinition(value);
  }

  public static class MatchTypeDefinition {
    private Object value;

    private MatchTypeDefinition(Object value) {
      this.value = value;
    }

    public <T, R> Match<R> addCase(Pattern<T, R> aCase) {
      Match<R> matchable = new Match<R>(this.value);
      matchable.addCase(aCase);
      return matchable;
    }

  }

  private Object value;
  private List<Pattern<?, R>> cases;

  private Match(Object value) {
    this.value = value;
    this.cases = new ArrayList<>();
  }

  public Match<R> addCase(Pattern<?, R> aCase) {
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
