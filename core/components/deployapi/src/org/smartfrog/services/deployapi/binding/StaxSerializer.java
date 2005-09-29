/**
 * From xfire.codehaus.org
 * (c) 2004 Envoi Solutions LLC
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
