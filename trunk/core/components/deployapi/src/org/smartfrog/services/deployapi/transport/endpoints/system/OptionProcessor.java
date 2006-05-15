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

import nu.xom.Element;
import nu.xom.Node;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.engine.OptionPropertyMap;
import org.smartfrog.services.deployapi.system.DeployApiConstants;
import org.smartfrog.services.deployapi.transport.endpoints.alpine.WsrfHandler;
import org.smartfrog.services.deployapi.transport.faults.BaseException;
import org.smartfrog.services.xml.java5.iterators.NodeIterator;
import org.smartfrog.projects.alpine.om.base.SoapElement;

/**
 * this processor extracts options from the request. If any option is marked
 * mustUnderstand and is not understood, it throws a fault created Aug 13, 2004
 * 1:53:05 PM
 */

public class OptionProcessor extends SystemProcessor {

    private boolean validateOnly = false;
    private String name;

    /**
     * log
     */
    private static final Log log = LogFactory.getLog(OptionProcessor.class);
    private static final String OPTION_ATTR_NAME = "name";
    private OptionPropertyMap optionPropertyMap;
    public static final String MUST_UNDERSTAND = "mustUnderstand";

    public OptionProcessor(WsrfHandler owner) {
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
    public Element process(SoapElement options) {
        if (options == null) {
            return null;
        }
        NodeIterator nodes=new NodeIterator(options);
        for(Node node:nodes) {
            if(!(node instanceof Element)) {
                //comments and things, presumably.
                continue;
            }
            Element option=(Element) node;
            boolean processed = false;

            String optionName = getOptionName(option);
            log.debug("option " + optionName);
            boolean mustUnderstand = getMustUnderstand(option);
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
                if (mustUnderstand) {
                    throwFailedToUnderstand(option);
                }
            }
        }
        return null;
    }

    private boolean getMustUnderstand(Element option) {
        XomHelper.getApiAttrValue(option, MUST_UNDERSTAND, false);
        boolean mustUnderstand = XomHelper.getBoolApiAttrValue(option, MUST_UNDERSTAND, false,false);
        return mustUnderstand;
    }

    private String getOptionName(Element option) {
        return XomHelper.getApiAttrValue(option, OPTION_ATTR_NAME,true);
    }

    /**
     * Get the (required) string attribute of an option
     * @param option
     * @return value of the api:string attr
     * @throws BaseException if missing
     */
    private String getOptionStringValue(Element option) {
        return XomHelper.getElementValue(option,"api:string");
    }

    /**
     * Get the (required) boolean attribute of an option
     *
     * @param option
     * @return value of the api:boolean attr
     * @throws BaseException if missing
     */
    private boolean getOptionBoolValue(Element option) {
        return XomHelper.getXsdBoolValue(XomHelper.getElementValue(option, "api:boolean"));
    }

    private int getOptionIntValue(Element option) {
        String val = XomHelper.getElementValue(option, "api:boolean");
        return Integer.valueOf(val);
    }

    private Element getOptionDataValue(Element option) {
        return XomHelper.getElement(option, "api:data",true);
    }

    private OptionPropertyMap getOptionPropertyMapValue(Element option) {
        OptionPropertyMap map=new OptionPropertyMap();
        Element xmlMap = XomHelper.getElement(option, "api:propertyMapOption", true);
        map.importMap(xmlMap);
        return map;
    }

    /**
* throw a fault declaring the option is not understood
*
* @param option always
*/
    private void throwFailedToUnderstand(Element option) {
        String uriName = getOptionName(option);
        log.warn("failed to process option " + uriName);
        throw raiseFault(DeployApiConstants.FAULT_NOT_UNDERSTOOD,
                "Did not recognise the mustUnderstand option" + uriName);
    }

    /**
     * extract the name option
     *
     * @param option
     */
    private void processNameOption(Element option) {
        assertNoXml(option);
        name = getOptionStringValue(option);
    }

    /**
     * extract validation option
     *
     * @param option
     */
    private void processValidateOption(Element option) {
        assertNoXml(option);
        validateOnly = getOptionBoolValue(option);
        log.debug("validateOnly :=" + validateOnly);
    }

    /**
     * extract values from the properties.
     *
     * @param option
     */
    private void processPropertiesOption(Element option) {
        assertNoXml(option);
        optionPropertyMap = getOptionPropertyMapValue(option);
    }

    /**
     * assert the xml element is empty
     *
     * @param option
     */
    private static void assertNoXml(Element option) {
        if (XomHelper.getElement(option, "api:data", false)!= null) {
            raiseBadArgumentFault("No XML allowed in " + option);
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

    /**
     * Get the (possibly null) runtime properties
     * @return runtime options
     */
    public OptionPropertyMap getOptionPropertyMap() {
        return optionPropertyMap;
    }
}


