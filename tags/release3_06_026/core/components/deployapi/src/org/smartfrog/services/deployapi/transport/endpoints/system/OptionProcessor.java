/** (C) Copyright 1998-2004 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.deployapi.transport.endpoints.system;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.ggf.xbeans.cddlm.api.OptionMapType;
import org.ggf.xbeans.cddlm.api.OptionType;
import org.smartfrog.services.deployapi.system.DeployApiConstants;
import org.smartfrog.services.deployapi.transport.endpoints.XmlBeansEndpoint;
import org.smartfrog.services.deployapi.transport.faults.BaseException;

import java.util.Properties;

/**
 * this processor extracts options from the request. If any option is marked
 * mustUnderstand and is not understood, it throws a fault created Aug 13, 2004
 * 1:53:05 PM
 */

public class OptionProcessor extends SystemProcessor {

    private Properties propertyMap = new Properties();
    private boolean validateOnly = false;
    private String name;

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(OptionProcessor.class);

    public OptionProcessor(XmlBeansEndpoint owner) {
        super(owner);
    }

    public OptionProcessor() {
        super(null);
    }

    /**
     * run through the option list. Ignore options we know nothing of but bail
     * out on things that are unknown and marked MustUnderstand
     *
     * @param options
     */
    public void process(OptionMapType options) {
        if (options == null) {
            return;
        }
        for (OptionType option : options.getOptionList()) {
            boolean processed = false;
            String optionName = option.getName();
            if (log.isDebugEnabled()) {
                log.debug("option " + optionName);
            }
            if (DeployApiConstants.OPTION_PROPERTIES.equals(optionName)) {
                processPropertiesOption(option);
                processed = true;
            }
            if (DeployApiConstants.OPTION_VALIDATE_ONLY.equals(optionName)) {
                processValidateOption(option);
                processed = true;
            }
            if (DeployApiConstants.OPTION_NAME.equals(optionName)) {
                processNameOption(option);
                processed = true;
            }
            if (!processed) {
                //not processed
                if (log.isDebugEnabled()) {
                    log.debug("Ignored header " + optionName);
                }
                if (option.getMustUnderstand()) {
                    throwFailedToUnderstand(option);
                }
            }
        }
    }

    /**
     * throw a fault declaring the option is not understood
     *
     * @param option always
     */
    private void throwFailedToUnderstand(OptionType option) {
        String uriName = option.getName();
        log.warn("failed to process option " + uriName);
        throw raiseFault(DeployApiConstants.FAULT_NOT_UNDERSTOOD,
                "Did not recognise the mustUnderstand option" + uriName);
    }

    /**
     * extract the name option
     *
     * @param option
     */
    private void processNameOption(OptionType option) {
        assertNoXml(option);
        name = option.getString();
    }

    /**
     * extract validation option
     *
     * @param option
     */
    private void processValidateOption(OptionType option) {
        assertNoXml(option);
        validateOnly = option.isSetBoolean();
        if (log.isDebugEnabled()) {
            log.debug("validateOnly :=" + validateOnly);
        }
    }

    /**
     * extract values from the properties.
     *
     * @param option
     */
    private void processPropertiesOption(OptionType option) {
        /*
        UnboundedXMLAnyNamespace xml = option.getData();
        MessageElement[] contents = xml.get_any();
        if (contents.length > 1) {
            throw raiseBadPropertiesData();
        }
        if (contents.length <= 0) {
            log.debug("empty data in properties");
            return;
        }
        MessageElement element = contents[0];
        final String message = "when parsing properties XML";
        Document doc = null;
        parseMessageFragment(element, message);
*/
        //TODO
        if (option.getMustUnderstand()) {
            throwFailedToUnderstand(option);
        }
    }

    private static BaseException raiseBadPropertiesData() {
        return raiseBadArgumentFault(
                "wrong structure of the properties option");
    }

    /**
     * assert the xml element is empty
     *
     * @param option
     */
    private static void assertNoXml(OptionType option) {
        if (option.getData() != null) {
            raiseBadArgumentFault("No XML allowed in " + option.getName());
        }
    }

    public boolean isValidateOnly() {
        return validateOnly;
    }

    /**
     * get the name of the request, or null
     *
     * @return
     */
    public String getName() {
        return name;
    }

    public void setPropertyMap(Properties propertyMap) {
        this.propertyMap = propertyMap;
    }

    public void setValidateOnly(boolean validateOnly) {
        this.validateOnly = validateOnly;
    }

    public void setName(String name) {
        this.name = name;
    }
}
