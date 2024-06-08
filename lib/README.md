# Pattern Matching for Java 8

This is a simple idea for an API for Java 8 Pattern Matching. Just some simple 
study that works well for every Java 8 Project and can be leveraged manually or as
a separate library. Consider it production ready!

## The idea
The idea here is really inspired by Rust and Scala (another JVM language) pattern 
matching capabilities. The pseudocode for them is like: 

```scala 
match x {
  case type -> type.toString
  case typeN when x == 0 -> "0"
  default -> "no value"
}
```

Here, we can see that we can match, as well, types, and types with conditions.
When matching, we have a single return type for all of declared cases, and a default 
return value when any of the other cases match as well.

## Limitations

In Java 8, we are limited by the actual compiler, because it don't leverages all 
the possible cases. Generally here, we can use the default extension mecanism of 
Java, by extending types, instead of increasing case braces. These are 
two facets of the problem of expression (any of the existent languages solves this 
well yet). But we can impose a runtime requirement of declaring the default case brace,
which can approximate the functionality present in Rust and Scala as well. 

## Implementatioon 

Let's start with a simple class: `Match`. The `Match` starts using a type parameter,
letting the JVM operates on it on any reference type:

```java 

public class Match<T> {

  static public <T> Match<T> a(T value) {
    return new Match<>(value);
  }

  private T value;

  private Match(T value) {
    this.value = value;
  }

}
```
That way, any reference type can be matched, which includes the boxed primitive types.
Now, how do we take the value and match against cases?
We use `Pattern` class. The basic case class should have a type to match against and a 
function to transform the value. We gonna leverage the `Builder` pattern to provide 
a fluent interface to help in the construction of the pattern:

```java 
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


}

```
