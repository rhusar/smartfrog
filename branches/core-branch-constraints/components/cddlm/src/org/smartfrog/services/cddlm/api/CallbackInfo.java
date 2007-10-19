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

import nu.xom.Element;
import org.apache.axis.AxisFault;
import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.cdl.XomAxisHelper;
import org.smartfrog.services.cddlm.generated.api.DeployApiConstants;
import org.smartfrog.services.cddlm.generated.api.types.NotificationInformationType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLOtherNamespace;

import javax.xml.parsers.ParserConfigurationException;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * helper for callbacks; isolates stuff to places
 * created Oct 26, 2004 3:07:41 PM
 */

public class CallbackInfo extends FaultRaiser {

    private String address;

    private String identifier;

    private URL url;

    public static final String ERROR_NO_CALLBACK = "No callback information";
    public static final String ERROR_NO_ADDRESS = "No address for callbacks";
    public static final String ERROR_NO_URI = "No URI in the address";
    public static final String ERROR_NO_CALLBACK_TYPE = "no callback type specified";
    public static final String ERROR_BAD_CALLBACK_URL = "Bad callback URL : ";

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

    public URI getType() {
        return DeployApiConstants.URI_CALLBACK_CDDLM_PROTOTYPE;
    }


    public URL getUrl() {
        return url;
    }

    public CallbackInfo() {
    }

    /**
     * create callback info
     * @return
     */
    public NotificationInformationType createCallback() {
        NotificationInformationType callbackInfo = new NotificationInformationType();
        String ns = DeployApiConstants.CALLBACK_CDDLM_PROTOTYPE;
        Element root = new Element(DeployApiConstants.CDDLM_PROTOTYPE_SUBSCRIPTION_ROOT_ELEMENT,
                ns);
        Element addr = new Element(DeployApiConstants.CDDLM_PROTOTYPE_SUBSCRIPTION_ADDRESS_ELEMENT,
                ns);
        addr.appendChild(address);
        root.appendChild(addr);
        UnboundedXMLOtherNamespace xml= new UnboundedXMLOtherNamespace();
        try {
            xml.set_any(XomAxisHelper.toArray(XomAxisHelper.convert(root)));
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
        callbackInfo.setType(DeployApiConstants.URI_CALLBACK_CDDLM_PROTOTYPE);
        callbackInfo.setIdentifier(identifier);
        callbackInfo.setSubscription(xml);
        return callbackInfo;
    }

    /**
     * fill in from a callback
     * @param info
     * @param required
     * @return
     * @throws AxisFault
     */
    public boolean extractCallback(NotificationInformationType info,
            boolean required)
        throws AxisFault {
        URI type = null;
        if (info == null) {
            if (!required) {
                address=null;
                identifier=null;
                return false;
            } else {
                throw raiseBadArgumentFault(ERROR_NO_CALLBACK);
            }
        }
        type = info.getType();
        if (type == null) {
            throw raiseBadArgumentFault(ERROR_NO_CALLBACK_TYPE);
        }
        String namespace = DeployApiConstants.CALLBACK_CDDLM_PROTOTYPE;
        if (!namespace.equals(type.toString())) {
            throw raiseUnsupportedCallbackFault(DeployApiConstants.UNSUPPORTED_CALLBACK_WIRE_MESSAGE);
        }
        UnboundedXMLOtherNamespace subscription = info.getSubscription();
        Element subInfo = XomAxisHelper.parse(subscription,
                "notification data");

        if (!DeployApiConstants.CDDLM_PROTOTYPE_SUBSCRIPTION_ROOT_ELEMENT.equals(subInfo.getLocalName())
                || !namespace.equals(subInfo.getNamespacePrefix())) {
            throw raiseBadArgumentFault("Wrong XML in the subscription information");
        }
        Element addrElt = subInfo.getFirstChildElement(namespace,
                DeployApiConstants.CDDLM_PROTOTYPE_SUBSCRIPTION_ADDRESS_ELEMENT);
        if (addrElt == null) {
            throw raiseBadArgumentFault(ERROR_NO_ADDRESS);
        }

        String uri = addrElt.getValue();
        if (uri == null) {
            throw raiseBadArgumentFault(ERROR_NO_URI);
        }
        try {
            url = makeURL(uri);
        } catch (MalformedURLException e) {
            throw raiseBadArgumentFault(ERROR_BAD_CALLBACK_URL +
                    uri.toString());
        }
        return true;
    }

}
