/*
 * This Java source file was generated by the Gradle 'init' task.
 */
package com.eronalves.patternmatching;

import org.junit.Test;
import static org.junit.Assert.*;

public class PatternMatchingTest {

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
}