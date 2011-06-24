/**
 *
 */
package org.smartfrog.services.assertions;

public interface CommonExceptionNames {
    /**
     * Smartfrog assertion. Value: {@value}
     */
    String EXCEPTION_SMARTFROG_ASSERTION = "org.smartfrog.services.assertions.SmartFrogAssertionException";
    /**
     * Text to look for in classname when seeking a resolution exception. Value: {@value}
     */
    String EXCEPTION_RESOLUTION = "org.smartfrog.sfcore.common.SmartFrogResolutionException";
    /**
     * Text to look for in classname when seeking a type resolution exception. Value: {@value}
     */
    String EXCEPTION_TYPERESOLUTION = "org.smartfrog.sfcore.common.SmartFrogTypeResolutionException";
    /**
     * Text to look for in classname when seeking a place resolution exception. Value: {@value}
     */
    String EXCEPTION_PLACERESOLUTION = "org.smartfrog.sfcore.common.SmartFrogPlaceResolutionException";
    /**
     * Text to look for in classname when seeking a link resolution exception. Value: {@value}
     */
    String EXCEPTION_LINKRESOLUTION = "org.smartfrog.sfcore.common.SmartFrogLinkResolutionException";
    /**
     * Text to look for in classname when seeking a function  resolution exception. Value: {@value}
     */
    String EXCEPTION_FUNCTIONRESOLUTION = "org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException";
    /**
     * Text to look for in classname when seeking a assertion resolution exception. Value: {@value}
     */
    String EXCEPTION_ASSERTIONRESOLUTION
            = "org.smartfrog.sfcore.common.SmartFrogAssertionResolutionException";
    /**
     * Text to look for in classname when seeking a lifecycle exception. Value: {@value}
     */
    String EXCEPTION_LIFECYCLE = "org.smartfrog.sfcore.common.SmartFrogLifecycleException";
    /**
     * Text to look for in classname when seeking a SmartFrogException. Value: {@value}
     */
    String EXCEPTION_SMARTFROG = "org.smartfrog.sfcore.common.SmartFrogException";
    /**
     * Text to look for in classname when seeking a liveness exception. Value: {@value}
     */

    String EXCEPTION_LIVENESS = "org.smartfrog.sfcore.common.SmartFrogLivenessException";
    /**
     * Text to look for in classname when seeking a SmartFrogDeploymentException. Value: {@value}
     */

    String EXCEPTION_DEPLOYMENT = "org.smartfrog.sfcore.common.SmartFrogDeploymentException";
    /**
     * Text to look for in classname when seeking a ClassCastException. Value: {@value}
     */
    String EXCEPTION_CLASSCAST = "java.lang.ClassCastException";
    /**
     * Text to look for in classname when seeking a ClassCastException. Value: {@value}
     */
    String EXCEPTION_CLASSNOTFOUND = "java.lang.ClassNotFoundException";
    /**
     * Text to look for in classname when seeking a SmartFrogParseException. Value: {@value}
     */
    String EXCEPTION_PARSE = "org.smartfrog.sfcore.common.SmartFrogParseException";
    /**
     * Text to look for in classname when seeking a SmartFrogCompileResolutionException. Value: {@value}
     */
    String EXCEPTION_COMPILE_RESOLUTION = "SmartFrogCompileResolutionException";
    /**
     * Text to look for in classname when seeking a SmartFrogLazyResolutionException. Value: {@value}
     */
    String EXCEPTION_SMARTFROG_LAZY_RESOLUTION_EXCEPTION =
            "org.smartfrog.sfcore.common.SmartFrogLazyResolutionException";
    String ERROR_UNRESOLVED_REFERENCE_LINK_RESOLUTION
                    = "Unresolved Reference during phase link resolution";
    String ERROR_UNRESOLVED_REFERENCE_TYPE_RESOLUTION
                            = "Unresolved Reference during phase type resolution";
}
