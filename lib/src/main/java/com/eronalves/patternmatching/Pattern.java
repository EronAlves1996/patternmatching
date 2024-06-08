package com.eronalves.patternmatching;

import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Pattern
 */
public class Pattern<T, R> {

  private Class<T> typeClass;
  private Predicate<T> condition;
  private Function<T, R> transformer;

  public static class PatternReturnDefiner<T> {

    Class<T> typeClass;
    Predicate<T> condition;

    private PatternReturnDefiner(Class<T> typeClass) {
      this.typeClass = typeClass;
    }

    public PatternReturnDefiner<T> condition(Predicate<T> condition) {
      this.condition = condition;
      return this;
    }

    public <R> Pattern<T, R> transformer(Function<T, R> transformer) {
      return new Pattern<T, R>(this.typeClass, this.condition,
          transformer);
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

  private Pattern(Class<T> typeClass, Predicate<T> condition,
      Function<T, R> transformer) {
    this.typeClass = typeClass;
    this.condition = condition;
    this.transformer = transformer;
  }

  public boolean test(Object other) {
    return typeClass.isInstance(other) && (condition == null
        || condition.test(typeClass.cast(other)));
  }

  public R apply(Object other) {
    return this.transformer.apply(typeClass.cast(other));
  }

}
