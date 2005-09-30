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

import nu.xom.XPathContext;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.ggf.cddlm.utils.QualifiedName;

import java.nio.charset.Charset;

/**
 * Any constants
 * created 12-Sep-2005 17:54:04
 */

public class Constants extends CddlmConstants {
    public static final String LOCALHOST = "localhost";
    public static final String LOCALHOST_IPV4 = "127.0.0.1";
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
            API_PACKAGE + CDL_FILENAME_DEPLOYMENT_API,
            WS_ADDRESSING_NAMESPACE,
            WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
            WS_ADDRESSING_NAMESPACE,
            WSRF_PACKAGE + XML_FILENAME_WS_ADDRESSING,
    };

    /**
     * @value
     */
    public static final String SMARTFROG_XML_VERSION = "1.0";

    /**
     * @value
     */
    public static final String DEFAULT_HOST = "127.0.0.1";

    /**
     * @value
     */
    public static final String DEFAULT_PATH = "/services/Portal";

    /**
     * @value
     */
    public static final int DEFAULT_SERVICE_PORT = 5050;


    /**
     * @value
     */
    public static final String DEFAULT_PROTOCOL = "http";

    public static final String FILE_EXTENSION_CDL = "cdl"; 
    
    /**
     * Context for queries
     */
    public static final XPathContext XOM_CONTEXT;

    static {
        XOM_CONTEXT = new XPathContext();
        XOM_CONTEXT.addNamespace("test", TEST_HELPER_NAMESPACE);
        XOM_CONTEXT.addNamespace("api", CDL_API_TYPES_NAMESPACE);
        XOM_CONTEXT.addNamespace("wsa2004", WS_ADDRESSING_2004_NAMESPACE);
        XOM_CONTEXT.addNamespace("wsa", WS_ADDRESSING_NAMESPACE);
        XOM_CONTEXT.addNamespace("wsnt", WSRF_WSNT_NAMESPACE);
        XOM_CONTEXT.addNamespace("cmp", CDL_CMP_TYPES_NAMESPACE);
        XOM_CONTEXT.addNamespace("cdl", XML_CDL_NAMESPACE);
        XOM_CONTEXT.addNamespace("wsrf-rp", WSRF_WSRP_NAMESPACE);
        XOM_CONTEXT.addNamespace("muws-p1-xs", MUWS_P1_NAMESPACE);
        XOM_CONTEXT.addNamespace("sf", SMARTFROG_NAMESPACE);
        XOM_CONTEXT.addNamespace("xpath", XPATH_NAMESPACE);
    }


    /**
     * Internal errors
     */
    public static final QualifiedName QNAME_SMARTFROG_INTERNAL_FAULT
            =new QualifiedName(SMARTFROG_NAMESPACE,"internalError");
    public static final String WSA_ELEMENT_ADDRESS = "Address";
    public static final String WSA_ATTR_PORTNAME = "PortName";
    public static final String WSA_ELT_SERVICENAME= "ServiceName";
    public static final String BUILD_INFO_IMPLEMENTATION_NAME = "SmartFrog CDDLM Implementation";
    public static final String BUILD_INFO_HOMEPAGE = "http://smartfrog.org/";
    public static final String BUILD_INFO_CDL_LANGUAGE = "CDL";
    public static final String BUILD_INFO_SF_LANGUAGE = "SmartFrog";
    public static final String SMARTFROG_VERSION = "1.0";
    public static final Charset CHARSET_SF_FILE = Charset.forName("ISO-8859-1");
    public static final Charset CHARSET_UTF8 = Charset.forName("UTF-8");

    /**
     * enum of lifecycle
     */
    public enum LifecycleStateEnum {
        undefined,
        instantiated,
        initialized,
        running,
        failed,
        terminated
    }



    public enum DeploymentLanguage {
        unknown("", ""),
        smartfrog(SMARTFROG_NAMESPACE, ".sf"),
        cdl(XML_CDL_NAMESPACE, ".cdl");

        private String namespace;
        private String extension;

        DeploymentLanguage(String namespace, String extension) {
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
            return getNamespace();
        }
    }
}
