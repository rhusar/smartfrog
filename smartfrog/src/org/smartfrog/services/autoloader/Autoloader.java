package org.smartfrog.services.autoloader;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.SFLoader;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.parser.Phases;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;
import java.io.IOException;

/**
 * Component that will autoload a .sf file to create a child component which is being de-referemced
 * Usually used as an immediate child of the ProcessCompound, to autoload components that are expected
 * to be named in the ProcessCompound as part of the environment.
 *
 * Do not use this to trigger a remote deployment, as it will lock up...
 */


public class Autoloader extends CompoundImpl implements Compound {
    final Reference prefixRef = new Reference(ReferencePart.here("URLPrefix"));
    final Reference validLoadsRef = new Reference(ReferencePart.here("validLoads"));
    final Reference postfixRef = new Reference(ReferencePart.here("URLPostfix"));
    final Reference matchesRef = new Reference(ReferencePart.here("matches"));
    final Reference languageRef = new Reference(ReferencePart.here("language"));

    private Vector validLoads = null;
    private String URLPrefix = "";
    private String URLPostfix = "";
    private String matches = "\\w+";
    private Pattern matchesPattern;
    private String language = "sf";
    public static final String ERROR_UNRESOLVED_AUTOLOAD_REFERENCE = "Unresolved Autoload Reference ";
    public static final String ERROR_REFERENCE_NAME_DOES_NOT_MATCH_THE_PATTERN = "reference name does not match the pattern: ";
    public static final String ERROR_REFERENCE_NAME_IS_NOT_IN_THE_LIST_OF_ALLOWED_LOADS = "reference name is not in the list of allowed loads :";

    public Autoloader() throws RemoteException {
    }

    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        validLoads = sfResolve(validLoadsRef, validLoads, false);
        URLPrefix = sfResolve(prefixRef, URLPrefix, false);
        URLPostfix = sfResolve(postfixRef, URLPostfix, false);
        matches = sfResolve(matchesRef, matches, false);
        matchesPattern = Pattern.compile(matches);
        language = sfResolve(languageRef, language, false);
    }

    /**
     * Override the superclasses actions with automatic loading of components on demand.
     *
     * {@inheritDoc}
     * @return the resolved object
     * @throws SmartFrogResolutionException other problems
     * @throws RemoteException network problems
     */
    public synchronized Object sfResolve(Reference name, int index) throws SmartFrogResolutionException, RemoteException {
        String namePart;
        try {
            return super.sfResolve(name, index);
        } catch (SmartFrogResolutionException resolutionException) {
            if (!sfIsStarted()) throw resolutionException;

            ReferencePart rp = name.elementAt(index);
            if (!(rp instanceof HereReferencePart)) {
                throw resolutionException;
            }

            Object np = ((HereReferencePart) rp).getValue();
            if (!(np instanceof String)) {
                throw resolutionException;
            }

            namePart = (String) np;
            if (!matchesName(namePart)) {
                throw new SmartFrogResolutionException(ERROR_REFERENCE_NAME_DOES_NOT_MATCH_THE_PATTERN +namePart,
                        this);
            }

            if (validLoads != null) {
                sfLog().debug("Checking "+namePart+" against the list of valid loads");
                if (!validLoads.contains(namePart)) {
                    throw new SmartFrogResolutionException(ERROR_REFERENCE_NAME_IS_NOT_IN_THE_LIST_OF_ALLOWED_LOADS + namePart,
                            this);
                }
            }

            if (sfContext.contains(namePart)) {
                sfLog().debug("reference name " + namePart + " is already in the context");
                throw resolutionException;
            }

            String fullName = URLPrefix + namePart + URLPostfix + "." + language;

           // construct name, and access through the class loader the resource.
           // create as new child of self
            try {
                InputStream is;                
                try {
                    // TODO: Figure out if it's OK to use the core classes class loading environment. I suspect it's not.
                    is = SFLoader.getInputStream(fullName, null);
                } catch (IOException e) {
                    throw SmartFrogResolutionException.forward(ERROR_UNRESOLVED_AUTOLOAD_REFERENCE + name
                            + " as " + fullName + " could not be loaded",
                            this.sfCompleteNameSafe(), e);
                }
                Phases p = new SFParser(language).sfParse(is);
                p = p.sfResolvePhases();
                ComponentDescription cd = p.sfAsComponentDescription();

                sfLog().info("deploying " + cd);

                sfCreateNewChild(namePart, cd, null);

            } catch (Exception ex) {
                throw new SmartFrogResolutionException(ERROR_UNRESOLVED_AUTOLOAD_REFERENCE + name + " at index " + index
                        +" as "+ fullName, ex,this);
            }
        }
        return super.sfResolve(name, index);
    }

    boolean matchesName(String s) {
        Matcher m = matchesPattern.matcher(s);
        return m.matches();
    }
}
