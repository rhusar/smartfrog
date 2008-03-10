/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

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
package org.smartfrog.services.xml.io;

import nu.xom.Document;
import nu.xom.Element;
import org.smartfrog.services.filesystem.FileIntf;
import org.smartfrog.services.filesystem.TupleDataSource;
import org.smartfrog.services.filesystem.TupleReaderThread;
import org.smartfrog.services.xml.impl.SerializedXomDocument;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;
import org.smartfrog.sfcore.utils.ListUtils;
import org.smartfrog.sfcore.prim.Prim;

import java.rmi.RemoteException;
import java.util.Vector;

/**
 * Create a Xom document from a file
 * <p/>
 * Created: 09-Mar-2008
 */
public class TuplesToXom extends AbstractXomFileWriter implements FileIntf {

    private SerializedXomDocument document;
    private String elementName;
    private Vector<String> fieldNames;
    private String namespaceURI;
    public static final String ATTR_NAMESPACE_URI = "namespaceURI";
    public static final String ATTR_ROOT = "root";
    public static final String ATTR_ELEMENT = "element";
    public static final String ATTR_FIELD_NAMES = "fieldNames";
    private boolean skipEmptyFields;
    public static final String ATTR_SKIP_EMPTY_FIELDS = "skipEmptyFields";
    public static final String ATTR_SOURCE = "source";
    private TupleReader reader;

    public TuplesToXom() throws RemoteException {
    }

    /**
     * Can be called to start components. Subclasses should override to provide
     * functionality Do not block in this call, but spawn off any main loops!
     *
     * @throws SmartFrogException failure while starting
     * @throws RemoteException In case of network/rmi error
     */
    public synchronized void sfStart() throws SmartFrogException, RemoteException {
        super.sfStart();
        bindWithDir(true, null);
        namespaceURI = sfResolve(ATTR_NAMESPACE_URI, "", true);
        String root = sfResolve(ATTR_ROOT, "", true);
        elementName = sfResolve(ATTR_ELEMENT, "", true);
        fieldNames = ListUtils.resolveStringList(
                this,
                new Reference(ATTR_FIELD_NAMES)
                , true);
        skipEmptyFields = sfResolve(ATTR_SKIP_EMPTY_FIELDS,false,true);
        Element rootelt = new Element(root, namespaceURI);
        Document doc = new Document(rootelt);
        document = new SerializedXomDocument(doc);
        TupleDataSource source = (TupleDataSource) sfResolve(ATTR_SOURCE, (Prim) null, true);
        reader = new TupleReader(source);
    }

    Document getDocument() {
        return document.getDocument();
    }

    Element getRootNode() {
        return document.getDocument().getRootElement();
    }

    private class TupleReader extends TupleReaderThread {

        public TupleReader(TupleDataSource source) {
            super(TuplesToXom.this, source, true);
        }


        /**
         * Process one line of the data source
         *
         * @param line line to process
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException network problems
         */
        @Override
        protected void processOneLine(String[] line) throws SmartFrogException, RemoteException {
            Element element=new Element(elementName, namespaceURI);
            try {
                for(int i=0;i<line.length;i++) {
                    String field=line[i];
                    String name= fieldNames.get(i);
                    if(field.length()==0 && skipEmptyFields) {
                        continue;
                    }
                    Element fieldElement=new Element(name, namespaceURI);
                    fieldElement.appendChild(field);
                    element.appendChild(fieldElement);
                }
                getRootNode().appendChild(element);
            } catch (ArrayIndexOutOfBoundsException e) {
                throw new SmartFrogException("Line "+getCount()+" is wider than the"
                + ATTR_FIELD_NAMES+" list",
                        TuplesToXom.this);
            }
        }

        /**
         * our work is done, let's write it out
         *
         * @throws SmartFrogException SmartFrog problems
         * @throws RemoteException network problems
         */
        @Override
        protected void onFinished() throws SmartFrogException, RemoteException {
            writeDocumentToFile(getDocument());
        }
    }
}
