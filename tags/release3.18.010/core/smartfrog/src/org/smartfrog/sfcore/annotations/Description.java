package org.smartfrog.sfcore.annotations;


import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * This declares a test that should be skipped. In Junit3, the test still needs to be renamed; this
 * attribute merely comments the test skipping and allows for it to be indexed
 */
@Retention(RetentionPolicy.RUNTIME)
public @interface Description {
    String value();
}
