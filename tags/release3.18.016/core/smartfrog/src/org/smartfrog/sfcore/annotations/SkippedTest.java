package org.smartfrog.sfcore.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This attribute is used to annotate tests that should be skipped, though we need a special test runner to
 * skip it reliably
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface SkippedTest {
    String value();
}
