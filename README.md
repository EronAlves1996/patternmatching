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
If we try to see how the compiler actually infers the return value from this 
match object, we gonna se that the type inference don't works correctly:

```java 
      Object run = Match.a(2)
          .addCase(Pattern.brace(String.class)
              .transformer(value -> "Is String"))
          .addCase(Pattern.brace(Integer.class)
              .transformer(value -> "Is Integer"))
          .run();
```

Let's correctly this on `Match` using gradual type composition! Let correctly another 
assumption about the types of `Match`:

* The actual value that enters the `Match` object don't need to be known. Gonna 
define it as generic `Object` type!
* The only type that needs to be known is the return type. The return gonna be 
inferred by the compiler. 

The full class definition turns as this:

```java 

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

```

If we try to infer the type now from the `Match.run` method, now it return the correct 
type:

```java
      String run = Match.a(2)
          .addCase(Pattern.brace(String.class)
              .transformer(value -> "Is String"))
          .addCase(Pattern.brace(Integer.class)
              .transformer(value -> "Is Integer"))
          .run();
```

The tests continues to pass.

Let's advance for another concept. Gonna define now a default brace, that will be 
a `Supplier<R>`. This supplier is a default value when any of the braces don't match, 
and since, any of them matches, the input value will not be resolved, and the user can 
supply a default output value:

```java 
public class Match<R> {

  // ...

  private Supplier<R> defaultSuppllier;

  // ...

  
  public Match<R> defaultValue(Supplier<R> d) {
    this.defaultSuppllier = d;
    return this;
  }

  // ...

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
```

That's it. If we run our test, one test gonna fail and the other gonna succeed. Let's 
refactor the tests to match to our new resolutions, and write a new test for no 
default value supllied:

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
            .defaultValue(() -> "Default Value")
            .run());
  }

  @Test
  public void testFailingMatchOnNoMatcher() {
    assertEquals("Default Value", Match.a(2)
        .addCase(Pattern.brace(String.class)
            .transformer(value -> "Is String"))
        .addCase(Pattern.brace(Long.class)
            .transformer(value -> "Is Integer"))
        .defaultValue(() -> "Default Value")
        .run());
  }

  @Test
  public void testFailingToRunWithNoDefault() {
    assertThrows(IllegalStateException.class, () -> {
      Match.a(2)
          .addCase(
              Pattern.brace(Integer.class).transformer(value -> "Is String"))
          .run();
    });
  }
```

That's great! Now we are close with the pattern matching functionallity of 
Java 17. We gonna go beyond and add a condition. The condition, when defined 
gonna be a function, tested inside the `Pattern` predicate. We gonna add this 
condition inside the `PatternReturnDefiner`, to force the user define the condition 
before declaring the transformer!


```java 
// Pattern.java 

public class Pattern<T, R> {

  // ...

  private Predicate<T> condition;

  // ...

  public static class PatternReturnDefiner<T> {

    // ...
    Predicate<T> condition;
  
    //...

    public PatternReturnDefiner<T> condition(Predicate<T> condition) {
      this.condition = condition;
      return this;
    }

    public <R> Pattern<T, R> transformer(Function<T, R> transformer) {
      return new Pattern<T, R>(this.typeClass, this.condition, 
            transformer);
    }
    
  }

  // ...


  private Pattern(Class<T> typeClass, Predicate<T> condition,
      Function<T, R> transformer) {
    this.typeClass = typeClass;
    this.condition = condition;
    this.transformer = transformer;
  }

  // ...


  public boolean test(Object other) {
    return typeClass.isInstance(other) && (condition == null
        || condition.test(typeClass.cast(other)));
  }

}
```

Great!! 
Now our implementation can support conditions on pattern matching.

Let's write a test to assert this new functionality!

```java
  @Test
  public void testMatchWithCondition() {
    assertEquals("Even", Match.a(2)
        .addCase(Pattern.brace(Integer.class)
            .condition(i -> i % 2 == 0)
            .transformer(value -> "Even"))
        .addCase(Pattern.brace(Integer.class)
            .condition(i -> i % 2 == 1)
            .transformer(value -> "Not Even"))
        .defaultValue(() -> "No Matches")
        .run());
  }
```
It concludes our experiment with a good API that leverages the new Java 17 API 
on previous versions!!
