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
import org.smartfrog.services.cddlm.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.generated.api.types.OptionType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;
import org.smartfrog.services.cddlm.generated.faults.FaultCodes;

import java.util.Properties;

/**
 * this processor extracts options from the request. If any option is marked
 * mustUnderstand and is not understood, it throws a fault created Aug 13, 2004
 * 1:53:05 PM
 */

public class OptionProcessor extends Processor {

    private Properties propertyMap = new Properties();
    private boolean validateOnly = false;

    private static final URI propertyURI;
    private static final URI validateURI;

    static {
        try {
            propertyURI =
            new URI(FaultCodes.OPTION_PROPERTIES);
            validateURI =
            new URI(FaultCodes.OPTION_VALIDATE_ONLY);
        } catch (URI.MalformedURIException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(DeployProcessor.class);

    public OptionProcessor(SmartFrogHostedEndpoint owner) {
        super(owner);
    }

    public OptionProcessor(SmartFrogHostedEndpoint owner, boolean validateOnly) {
        this(!validateOnly);
    }

    public OptionProcessor(boolean validateOnly) {
        super(null);
        this.validateOnly = validateOnly;
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
            URI nameURI = option.getName();
            final String name = nameURI.toString();
            if (log.isDebugEnabled()) {
                log.debug("option " + name);
            }
            if (propertyURI.equals(nameURI)) {
                processPropertiesOption(option);
                processed = true;
            }
            if (validateURI.equals(nameURI)) {
                processValidateOption(option);
                processed = true;
            }
            if (!processed) {
                //not processed
                if (log.isDebugEnabled()) {
                    log.debug("Ignored header " + name);
                }
                if (option.isMustUnderstand()) {
                    log.warn("failed to process option " + name);
                    throw raiseFault(FaultCodes.FAULT_NOT_UNDERSTOOD, name);
                }
            }
        }
    }

    private void processValidateOption(OptionType option) throws AxisFault {
        assertNoXml(option);
        validateOnly = option.is_boolean();
        if (log.isDebugEnabled()) {
            log.debug("validateOnly :=" + validateOnly);
        }
    }

    private void processPropertiesOption(OptionType option) throws AxisFault {
        UnboundedXMLAnyNamespace xml = option.getXml();
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
        throwNotImplemented();

    }

    private AxisFault raiseBadPropertiesData() {
        return raiseBadArgumentFault(
                "wrong structure of the properties option");
    }

    /**
     * assert the xml element is empty
     *
     * @param option
     * @throws AxisFault
     */
    private void assertNoXml(OptionType option) throws AxisFault {
        if (option.getXml() != null) {
            raiseBadArgumentFault("No XML allowed in " + option.getName());
        }
    }

    public boolean isValidateOnly() {
        return validateOnly;
    }

}
