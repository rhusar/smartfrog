/** (C) Copyright 2005 Hewlett-Packard Development Company, LP

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 2.1 of the License, or (at your option) any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA

 For more information: www.smartfrog.org

 */

package org.smartfrog.services.deployapi.system;

import org.ggf.cddlm.generated.api.CddlmConstants;

/**

 */
public enum DeploymentLanguage {
    unknown("", "","unknown"),
    smartfrog(CddlmConstants.SMARTFROG_NAMESPACE, ".sf","SmartFrog description"),
    cdl(CddlmConstants.XML_CDL_NAMESPACE, ".cdl","CDDLM descriptor");

    private String namespace;
    private String extension;
    private String description;

    DeploymentLanguage(String namespace, String extension, String description) {
       this.namespace = namespace;
       this.extension = extension;
       this.description = description;
   }

    public String getNamespace() {
        return namespace;
    }

    public String getExtension() {
        return extension;
    }

    public String getDescription() {
        return description;
    }

    public boolean namespaceEquals(String ns) {
        return namespace.equals(ns);
    }

    /**
     * map from a namespace to a language
     *
     * @param ns
     * @return the language, or #unknown if not known
     */
    public static DeploymentLanguage eval(String ns) {
        if (smartfrog.namespaceEquals(ns)) {
            return smartfrog;
        }
        if (cdl.namespaceEquals(ns)) {
            return cdl;
        } else return unknown;
    }


    /**
     * Returns the name of this enum constant, as contained in the
     * declaration.  This method may be overridden, though it typically
     * isn't necessary or desirable.  An enum type should override this
     * method when a more "programmer-friendly" string form exists.
     *
     * @return the name of this enum constant
     */
    public String toString() {
        return getDescription();
    }
}
