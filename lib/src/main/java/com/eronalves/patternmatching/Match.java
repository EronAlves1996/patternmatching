package com.eronalves.patternmatching;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

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
  private Supplier<R> defaultSuppllier;

  private Match(Object value) {
    this.value = value;
    this.cases = new ArrayList<>();
  }

  public Match<R> addCase(Pattern<?, R> aCase) {
    this.cases.add(aCase);
    return this;
  }

  public Match<R> defaultValue(Supplier<R> d) {
    this.defaultSuppllier = d;
    return this;
  }

  public R run() {
    if (this.defaultSuppllier == null) {
      throw new IllegalStateException("Default supplier not defined!");
    }
    for (Pattern<?, R> aCase : cases) {
      if (aCase.test(this.value)) {
        return aCase.apply(this.value);
      }
    }
    return defaultSuppllier.get();
  }

}
