package com.eronalves.patternmatching;

import java.util.function.Function;

/**
 * Pattern
 */
public class Pattern<T, R> {

  private Class<T> typeClass;
  private Function<T, R> transformer;

  public static class PatternReturnDefiner<T> {

    Class<T> typeClass;

    private PatternReturnDefiner(Class<T> typeClass) {
      this.typeClass = typeClass;
    }

    public <R> Pattern<T, R> transformer(Function<T, R> transformer) {
      return new Pattern<T, R>(this.typeClass, transformer);
    }
  }

  public static class PatternBuilder<T> {

    private PatternBuilder() {
    }

    public PatternReturnDefiner<T> type(Class<T> typeClass) {
      return new PatternReturnDefiner<>(typeClass);
    }

  }

  public static <T> PatternReturnDefiner<T> brace(Class<T> typeClass) {
    return new PatternBuilder<T>()
        .type(typeClass);
  }

  private Pattern(Class<T> typeClass, Function<T, R> transformer) {
    this.typeClass = typeClass;
    this.transformer = transformer;
  }

  public boolean test(Object other) {
    return typeClass.isInstance(other);
  }

  public R apply(Object other) {
    return this.transformer.apply(typeClass.cast(other));
  }

}
