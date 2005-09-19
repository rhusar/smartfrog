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
 * Any constants
 * created 12-Sep-2005 17:54:04
 */

public class Constants extends CddlmConstants {
    public static final String LOCALHOST = "localhost";
    public static final String ERROR_CREATE_UNSUPPORTED_HOST = "Unsupported Host";
    public static final String ERROR_NOT_DOCLIT = "only doc/lit SOAP supported";

    protected static final String PACKAGE_BASE = "org/ggf/cddlm/";
    /**
     * where all the WSRF files really live {@value}
     */
    private static final String WSRF_PACKAGE = PACKAGE_BASE
            + XML_FILENAME_WSRF_DIRECTORY;

    /**
     * where the API files really live {@value}
     */
    private static final String API_PACKAGE = PACKAGE_BASE
            + CDL_FILENAME_XML_DIRECTORY;


    /**
     * This maps from namespaces to resources in our classpath {@value}
     */
    public static final String WSRF_MAPPINGS[] = {
            WS_ADDRESSING_NAMESPACE,
            WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
            WS_ADDRESSING_NAMESPACE,
            WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
    };

    /**
     * This maps from namespaces to resources in our classpath {@value}
     */
    public static final String CDDLM_MAPPINGS[] = {
            XML_CDL_NAMESPACE,
                API_PACKAGE + CDL_FILENAME_XML_CDL,
            CDL_API_TYPES_NAMESPACE,
                API_PACKAGE +CDL_FILENAME_DEPLOYMENT_API,
            WS_ADDRESSING_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
            WS_ADDRESSING_NAMESPACE,
                WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
    };
    public static final String SMARTFROG_XML_VERSION = "1.0";

    /**
     * enum of lifecycle
     */
    public enum LifecycleStateEnum {
        undefined ,
        instantiated ,
        initialized,
        running ,
        failed ,
        terminated
    }

    public enum DeploymentLanguage {
        unknown("",""),
        smartfrog(SMARTFROG_NAMESPACE,".sf"),
        cdl(XML_CDL_NAMESPACE,".cdl");

        private String namespace;
        private String extension;

        DeploymentLanguage(String namespace,String extension) {
           this.namespace = namespace;
           this.extension = extension;
        }

        public String getNamespace() {
            return namespace;
        }

        public String getExtension() {
            return extension;
        }

        public boolean namespaceEquals(String ns) {
            return namespace.equals(ns);
        }

        /**
         * map from a namespace to a language
         * @param ns
         * @return the language, or #unknown if not known
         */
        public static DeploymentLanguage eval(String ns) {
            if(smartfrog.namespaceEquals(ns)) {
                return smartfrog;
            }
            if(cdl.namespaceEquals(ns)) {
                return cdl;
            }
            else return unknown;
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
            return getNamespace();
        }
    }
}
