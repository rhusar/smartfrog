package org.smartfrog.services.autoloader;

import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.reference.ReferencePart;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.common.SmartFrogResolutionException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.compound.CompoundImpl;
import org.smartfrog.sfcore.compound.Compound;
import org.smartfrog.sfcore.security.SFClassLoader;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.parser.SFParser;
import org.smartfrog.sfcore.parser.Phases;

import java.rmi.RemoteException;
import java.util.Vector;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.InputStream;

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
    final Reference langaugeRef = new Reference(ReferencePart.here("language"));

    Vector validLoads = null;
    String URLPrefix = "";
    String URLPostfix = "";
    String matches = "\\w+";
    Pattern matchesPattern;
    String language = "sf";

    public Autoloader() throws RemoteException {
    }

    public void sfDeploy() throws SmartFrogException, RemoteException {
        super.sfDeploy();
        validLoads = sfResolve(validLoadsRef, validLoads, false);
        URLPrefix = sfResolve(prefixRef, URLPrefix, false);
        URLPostfix = sfResolve(postfixRef, URLPostfix, false);
        matches = sfResolve(matchesRef, matches, false);
        matchesPattern = Pattern.compile(matches);
        language = sfResolve(langaugeRef, language, false);
    }

    public synchronized Object sfResolve(Reference name, int index) throws SmartFrogResolutionException, RemoteException {
        String namePart;
        try {
            return super.sfResolve(name, index);
        } catch (SmartFrogResolutionException e) {
            ReferencePart rp = name.elementAt(index);
            if (!(rp instanceof HereReferencePart)) throw e;

            Object np = ((HereReferencePart) rp).getValue();
            if (!(np instanceof String)) throw e;

            namePart = (String) np;
            if (matchesName(namePart)) throw e;
            if (validLoads != null && !validLoads.contains(namePart)) throw e;

            if (sfContext.contains(namePart)) throw e;

            String fullName = URLPrefix + namePart + URLPostfix + "." + language;

           // construct name, and access through the class loader the resource.
           // create as new child of self
            try {
                InputStream is = SFClassLoader.getResourceAsStream(fullName);
                Phases p = new SFParser(language).sfParse(is);
                p = p.sfResolvePhases();
                ComponentDescription cd = p.sfAsComponentDescription();

                System.out.println("deploying " + cd);

                sfCreateNewChild(namePart, cd, null);
            } catch (Exception ex) {
                throw new SmartFrogResolutionException("Error in autoloader: resolving " + name + " at index " + index, ex);
            }
        }
        return super.sfResolve(name, index);
    }

    boolean matchesName(String s) {
        Matcher m = matchesPattern.matcher(s);
        return m.matches();
    }
}
