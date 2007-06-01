package org.smartfrog.sfcore.security;

public class SmartFrogCorePropertySecurity {

    private SmartFrogCorePropertySecurity() {}

    /**
     * Base for all smartfrog properties. All properties looked up by classes
     * in SmartFrog use this as a base, add the package name and then the
     * property id to look up.
     *
     * Value {@value}
     * @see org.smartfrog.SFSystem
     */
    public static final String propBase = "org.smartfrog.";
}
