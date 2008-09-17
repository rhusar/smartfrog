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

import nu.xom.Element;
import nu.xom.Elements;
import org.ggf.cddlm.generated.api.CddlmConstants;
import org.smartfrog.projects.alpine.om.base.SoapElement;
import org.smartfrog.services.cddlm.cdl.base.LifecycleStateEnum;
import org.smartfrog.services.deployapi.binding.XomHelper;
import org.smartfrog.services.deployapi.transport.faults.FaultRaiser;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.nio.charset.Charset;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;

/**
 * created 20-Sep-2005 17:07:38
 */

public class Utils {

    protected Utils() {
    }



    /**
     * create a new uuid-style id
     * @return
     */
    public static String createNewID() {
        UUID uuid = UUID.randomUUID();
        String s = uuid.toString();
        s = s.replace("-", "_");
        return "uuid_" + s;
    }


    /**
     * Close any open stream; ignore any errors.
     * @param stream
     */
    public static void close(Closeable stream) {
        try {
            if(stream!=null) {
                stream.close();
            }
        } catch (IOException e) {
            //ignore
        }
    }

    /**
     * Save text to a file
     * @param dest destination file
     * @param contents text to save
     * @param cs charset
     * @throws IOException
     */
    public static void saveToFile(File dest, String contents,Charset cs) throws IOException {
        OutputStream out=new BufferedOutputStream(new FileOutputStream(dest));
        Writer writer=new OutputStreamWriter(out,cs);
        try {
            writer.write(contents);
        } finally {
            writer.close();
        }
    }

    public static void saveToBinaryFile(File dest, byte[] data) throws IOException {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(dest);
            out.write(data);
        } finally {
            close(out);
        }
    }

    /**
     * Load a file with a given charset into a buffer
     * @param file
     * @param cs
     * @return
     * @throws IOException
     */
    public static String loadFile(File file, Charset cs) throws IOException {
        InputStream in;
        in = new FileInputStream(file);
        return loadInputStream(in, cs);
    }


    /**
     * Load an input stream
     * @param in
     * @param cs
     * @return
     * @throws IOException
     */
    public static String loadInputStream(InputStream in, Charset cs) throws
            IOException {
        BufferedInputStream buffIn = null;
        InputStreamReader reader = null;
        try {
            buffIn = new BufferedInputStream(in);
            reader = new InputStreamReader(buffIn,cs);
            StringWriter dest=new StringWriter();
            int ch;
            while((ch = reader.read())>=0) {
                dest.write(ch);
            }
            return dest.toString();
        } finally {
            close(reader);
        }
    }

    

    
    public static String toIsoTime(Date timestamp) {
        DateFormat format= makeIsoDateFormatter();
        return format.format(timestamp);

    }

    public static DateFormat makeIsoDateFormatter() {
        return new SimpleDateFormat(
                "yyyy-MM-dd'T'HH:mm:ss.SSSZ");
    }

    public static Element makeAddress(String url, String namespace) {
        Element endpoint =new Element("EndpointReference",namespace);
        Element address =new Element(Constants.WSA_ELEMENT_ADDRESS,namespace);
        address.appendChild(url);
        endpoint.appendChild(address);
        return endpoint;
    }

    /**
     * Go from XML to types
     * @param source
     * @return
     * @throws
     */
    public static LifecycleStateEnum parseCmpState(Element source) {
        Element cmpstate;
        if (source.getLocalName().equals(LifecycleStateEnum.STATE)
                && CddlmConstants.CDL_CMP_TYPES_NAMESPACE.equals(source.getNamespaceURI())) {
            cmpstate = source;
        } else {
            cmpstate = source.getFirstChildElement(LifecycleStateEnum.STATE,
                    CddlmConstants.CDL_CMP_TYPES_NAMESPACE);
            if (cmpstate == null) {
                throw FaultRaiser.raiseBadArgumentFault(
                        "No cmp:State element under " + source.toXML());
            }
        }
        Elements elements = cmpstate.getChildElements();
        if (elements.size() == 0) {
            throw FaultRaiser.raiseBadArgumentFault("No elements under cmp:State " + cmpstate.toXML());
        }
        Element child = elements.get(0);
        if (!CddlmConstants.CDL_CMP_TYPES_NAMESPACE.equals(child.getNamespaceURI())) {
            throw FaultRaiser.raiseBadArgumentFault(
                    "First state element is not in the expected namespace " + child);
        }
        String statename = child.getLocalName();
        return LifecycleStateEnum.extract(statename);
    }

    /**
     * Return a cmp:State element containing the local state
     * as a direct child. The name of the local state is defined
     * by the xmlName value of the state.
     * @return the state in XML
     * @param state the state to parse
     */
    public static SoapElement toCmpState(LifecycleStateEnum state) {
        SoapElement parent = XomHelper.cmpElement(LifecycleStateEnum.STATE);
        SoapElement child = XomHelper.cmpElement(state.getXmlName());
        parent.appendChild(child);
        return parent;
    }
}
