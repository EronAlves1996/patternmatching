package com.eronalves.patternmatching;

import java.util.function.Function;

/**
 * Pattern
 */
public class Pattern<T, R> {

  private Class<T> typeClass;
  private Function<T, R> transformer;

  private static class PatternBuilder<T, R> {

    Pattern<T, R> pattern;

    private PatternBuilder() {
      this.pattern = new Pattern<>();
    }

    public PatternBuilder<T, R> type(Class<T> typeClass) {
      this.pattern.typeClass = typeClass;
      return this;
    }

    public PatternBuilder<T, R> transformer(Function<T, R> transformer) {
      this.pattern.transformer = transformer;
      return this;
    }

    public Pattern<T, R> build() {
      return this.pattern;
    }

  }

  public static <T, R> PatternBuilder<T, R> brace(Class<T> typeClass) {
    return new PatternBuilder<T, R>()
        .type(typeClass);
  }

  public boolean test(Object other) {
    return typeClass.isInstance(other);
  }

  public R apply(Object other) {
    return this.transformer.apply(typeClass.cast(other));
  }

}
