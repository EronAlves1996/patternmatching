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
