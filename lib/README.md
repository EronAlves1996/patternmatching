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

Other thing that we gonna make is build the types gradually, to make the Java Compiler 
infer the return types correctly, making the user experience go smoothly.

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
We use `Pattern` class. The basic class should have a type to match against and a 
function to transform the value. We gonna leverage a functional interface and build 
gradually the final type, to make the Java compiler infer the types correctly:

```java 
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

}

```

Starting the process to get the builder, we need a public method. Let's leverage the 
`brace` (`for` and `case` are reserved words :/) method for that, which returns 
a `PatternBuilder`, and build upon it:

```java 
public static <T> PatternBuilder<T> brace(Class<T> typeClass) {
    return new PatternBuilder<T>()
        .type(typeClass);
}
```

We have a basic implementation here!! 
Now, we gonna make our `Match` class run each cases and provide a basic throws 
if any of the cases match. Let's update too the type parameters of `Match`, to 
include a generic return value. Let's add a method `test` to `Pattern` class 
to make this class test a generic `Object` against it's specification and a method 
`apply` to make to transform the object:

```java
// Pattern.java 


public boolean test(Object other) {
  return typeClass.isInstance(other);
}

public R apply(Object other) {
  return this.transformer.apply(typeClass.cast(other));
}

// Match.java 
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

```

Let's build a test to validate the basic assumptions:

* Match succesfully against a type and return a value:


```java

  @Test
  public void testMatchSuccesfullyAnInteger() {
    assertEquals("Is Integer",
        Match.a(2)
            .addCase(Pattern.brace(String.class)
                .transformer(value -> "Is String"))
            .addCase(
                Pattern.brace(Integer.class)
                    .transformer(value -> "Is Integer"))
            .run());
  }

```

* Throw an error if the `Match` failed to match: 

```java 
  @Test
  public void testFailingMatchOnNoMatcher() {
    assertThrows(IllegalStateException.class, () -> {
      Match.a(2)
          .addCase(Pattern.brace(String.class)
              .transformer(value -> "Is String"))
          .addCase(Pattern.brace(Integer.class)
              .transformer(value -> "Is Integer"))
          .run();
    });
  }
```

These two tests actually works, but wait!
