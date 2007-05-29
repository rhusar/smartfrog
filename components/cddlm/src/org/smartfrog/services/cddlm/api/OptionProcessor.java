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
package org.smartfrog.services.cddlm.api;

import nu.xom.Document;
import org.apache.axis.AxisFault;
import org.apache.axis.message.MessageElement;
import org.apache.axis.types.URI;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.axis.SmartFrogHostedEndpoint;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.generated.api.types.OptionType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;

import java.util.Properties;

/**
 * this processor extracts options from the request. If any option is marked
 * mustUnderstand and is not understood, it throws a fault created Aug 13, 2004
 * 1:53:05 PM
 */

public class OptionProcessor extends Processor {

    private Properties propertyMap = new Properties();
    private boolean validateOnly = false;
    private String name;

    private static final URI propertyURI;
    private static final URI validateURI;
    private static final URI nameURI;


    static {
        try {
            propertyURI =
                    new URI(DeployApiConstants.OPTION_PROPERTIES);
            validateURI =
                    new URI(DeployApiConstants.OPTION_VALIDATE_ONLY);
            nameURI =
                    new URI(DeployApiConstants.OPTION_NAME);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(CreateProcessor.class);

    public OptionProcessor(SmartFrogHostedEndpoint owner) {
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
     * @throws AxisFault
     */
    public void process(OptionMapType options) throws AxisFault {
        if (options == null) {
            return;
        }
        OptionType[] array = options.getOption();
        int len = array.length;
        for (int i = 0; i < len; i++) {
            boolean processed = false;
            OptionType option = array[i];
            URI uri = option.getName();
            final String optionName = uri.toString();
            if (log.isDebugEnabled()) {
                log.debug("option " + optionName);
            }
            if (propertyURI.equals(uri)) {
                processPropertiesOption(option);
                processed = true;
            }
            if (validateURI.equals(uri)) {
                processValidateOption(option);
                processed = true;
            }
            if (nameURI.equals(uri)) {
                processNameOption(option);
                processed = true;
            }
            if (!processed) {
                //not processed
                if (log.isDebugEnabled()) {
                    log.debug("Ignored header " + optionName);
                }
                if (option.getMustUnderstand().booleanValue()) {
                    throwFailedToUnderstand(option);
                }
            }
        }
    }

    /**
     * throw a fault declaring the option is not understood
     *
     * @param option
     * @throws AxisFault always
     */
    private void throwFailedToUnderstand(OptionType option) throws AxisFault {
        URI uri = option.getName();
        final String uriName = uri.toString();
        log.warn("failed to process option " + uriName);
        throw raiseFault(DeployApiConstants.FAULT_NOT_UNDERSTOOD,
                "Did not recognise the mustUnderstand option" + uriName);
    }

    /**
     * extract the name option
     *
     * @param option
     * @throws AxisFault
     */
    private void processNameOption(OptionType option) throws AxisFault {
        assertNoXml(option);
        name = option.getString();
    }

    /**
     * extract validation option
     *
     * @param option
     * @throws AxisFault
     */
    private void processValidateOption(OptionType option) throws AxisFault {
        assertNoXml(option);
        validateOnly = option.is_boolean();
        if (log.isDebugEnabled()) {
            log.debug("validateOnly :=" + validateOnly);
        }
    }

    /**
     * extract values from the properties.
     *
     * @param option
     * @throws AxisFault
     */
    private void processPropertiesOption(OptionType option) throws AxisFault {
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

        //TODO
        if (option.getMustUnderstand().booleanValue()) {
            throwFailedToUnderstand(option);
        }
    }

    private static AxisFault raiseBadPropertiesData() {
        return raiseBadArgumentFault(
                "wrong structure of the properties option");
    }

    /**
     * assert the xml element is empty
     *
     * @param option
     * @throws AxisFault
     */
    private static void assertNoXml(OptionType option) throws AxisFault {
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
