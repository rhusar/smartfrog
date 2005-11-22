/**
 From xfire.codehaus.org

 Copyright (c) 2004 Envoi Solutions LLC

 Permission is hereby granted, free of charge, to any person obtaining a copy of
 this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights to use,
 copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the
 Software, and to permit persons to whom the Software is furnished to do so,
 subject to the following conditions:

 The above copyright notice and this permission notice shall be included in
 all copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES
 OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
 CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
 TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
 SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.

 */

package org.smartfrog.services.deployapi.binding;

import nu.xom.Attribute;
import nu.xom.Comment;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.Node;
import nu.xom.Text;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StaxSerializer
{
    public void writeDocument(Document doc, XMLStreamWriter writer)
        throws XMLStreamException
    {
        writer.writeStartDocument("1.0");
        writeElement(doc.getRootElement(), writer);
        writer.writeEndDocument();
    }

    public void writeElement(Element e, XMLStreamWriter writer)
        throws XMLStreamException
    {
        // need to check if the namespace is declared before we write the
        // start element because that will put the namespace in the context.
        String elPrefix = e.getNamespacePrefix();
        String elUri = e.getNamespaceURI();

        String boundPrefix = writer.getPrefix(elUri);
        boolean writeElementNS = false;
        if ( boundPrefix == null || !elPrefix.equals(boundPrefix) )
        {   
            writeElementNS = true;
        }
        
        writer.writeStartElement(elPrefix, e.getLocalName(), elUri);

        for (int i = 0; i < e.getNamespaceDeclarationCount(); i++)
        {
            String prefix = e.getNamespacePrefix(i);
            String uri = e.getNamespaceURI(prefix);

            writer.writeNamespace(prefix, uri);
            
            if (elUri.equals(uri) && elPrefix.equals(prefix))
            {
                writeElementNS = false;
            }
        }

        if (writeElementNS)
        {
            if ( elPrefix == null || elPrefix.length() ==  0 )
            {
                writer.writeDefaultNamespace(elUri);
            }
            else
            {
                writer.writeNamespace(elPrefix, elUri);
            }
        }
        
        for (int i = 0; i < e.getAttributeCount(); i++)
        {
            Attribute attr = e.getAttribute(i);
            String attPrefix= attr.getNamespacePrefix();
            String attUri = attr.getNamespaceURI();
            
            if (attUri == null)
                writer.writeAttribute(attr.getLocalName(), attr.getValue());
            else
                writer.writeAttribute(attPrefix, attUri, attr.getLocalName(), attr.getValue());
        }

        for (int i = 0; i < e.getChildCount(); i++)
        {
            Node n = (Node) e.getChild(i);
            if (n instanceof Text)
            {
                writer.writeCharacters(n.toXML());
            }
            else if (n instanceof Element)
            {
                writeElement((Element) n, writer);
            }
            else if (n instanceof Comment)
            {
                writer.writeComment(n.getValue());
            }
        }

        writer.writeEndElement();
    }

    /**
     * @param writer
     * @param prefix
     * @param uri
     * @throws XMLStreamException
     */
    private boolean isDeclared(XMLStreamWriter writer, String prefix, String uri)
        throws XMLStreamException
    {
        String decPrefix = writer.getPrefix(uri);
        return (decPrefix != null && decPrefix.equals(prefix));
    }
}
