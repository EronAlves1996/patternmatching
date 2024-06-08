package com.eronalves.patternmatching;

import java.util.function.Function;

/**
 * Pattern
 */
public class Pattern<T, R> {

  private Class<T> typeClass;
  private Function<T, R> transformer;

  private class PatternBuilder {

    Pattern<T, R> pattern;

    private PatternBuilder(){
      this.pattern = new Pattern<>();
    }

    public PatternBuilder type(Class<T> typeClass) {
      this.pattern.typeClass = type;
      return this;
    }

    public PatternBuilder transformer(Function<T, R> tranformer) {
      this.pattern.transformer = transformer;
      return this;
    }

    public Pattern<T, R> build() {
      return this.pattern;
    }

  }

  static<T>PatternBuilder<T>case(

  Class<T> type)
  {
    return new PatternBuilder<>()
        .type(type);
  }

}
