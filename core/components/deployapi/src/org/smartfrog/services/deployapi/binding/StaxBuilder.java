/**
 * From xfire.codehaus.org
 * (c) 2004 Envoi Solutions LLC
 */

package org.smartfrog.services.deployapi.binding;

import java.io.InputStream;
import java.io.Reader;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

import nu.xom.Attribute;
import nu.xom.Document;
import nu.xom.Element;
import nu.xom.NodeFactory;
import nu.xom.Nodes;

public class StaxBuilder
{
    private NodeFactory factory;
    private XMLInputFactory ifactory;
    
    public StaxBuilder()
    {
        this.factory = new NodeFactory();
        ifactory = XMLInputFactory.newInstance();
    }
    
    public StaxBuilder(NodeFactory factory)
    {
        this.factory = factory;
        ifactory = XMLInputFactory.newInstance();
    }
    
    public StaxBuilder(XMLInputFactory ifactory)
    {
        this.factory = new NodeFactory();
        this.ifactory = ifactory;
    }

    public Document build(InputStream is) 
        throws XMLStreamException
    {
        return build(ifactory.createXMLStreamReader(is));
    }
    
    public Document build(Reader reader) 
        throws XMLStreamException
    {
        return build(ifactory.createXMLStreamReader(reader));
    }
    
    public Document build(XMLStreamReader reader) 
        throws XMLStreamException
    {
        Element rootEl = null;
        Document doc = null;
        
        int event = reader.getEventType();
        while ( true )
        {
            switch( event )
            {
            case XMLStreamConstants.START_ELEMENT:
                rootEl = createElement(reader);
                doc.setRootElement(rootEl);
                
                declareNamespaces(reader, rootEl);
                
                for ( int i = 0; i < reader.getAttributeCount(); i++ )
                {
                    Nodes nodes = createAttribute(reader, i);

                    for (int j = 0; j < nodes.size(); j++)
                    {
                        rootEl.addAttribute((Attribute)nodes.get(j));
                    }
                }
                
                reader.next();
                buildElement(rootEl, reader);

                break;
            case XMLStreamConstants.END_ELEMENT:
                break;
            case XMLStreamConstants.CHARACTERS:
                rootEl.appendChild(reader.getText());
                break;
            case XMLStreamConstants.START_DOCUMENT:
                doc = factory.startMakingDocument();
                break;
            case XMLStreamConstants.END_DOCUMENT:
                factory.finishMakingDocument(doc);
                return doc;
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.ATTRIBUTE:
            case XMLStreamConstants.NAMESPACE:
            default:
                break;
            }
            
            event = reader.next();
        }
    }

    private void declareNamespaces(XMLStreamReader reader, Element rootEl)
    {
        for ( int i = 0; i < reader.getNamespaceCount(); i++ )
        {
            String uri = reader.getNamespaceURI(i);
            String prefix = reader.getNamespacePrefix(i);
            if (prefix == null) prefix = "";
            
            String decUri = rootEl.getNamespaceURI(prefix);
            if (decUri == null || !decUri.equals(uri))
            {
                rootEl.addNamespaceDeclaration(prefix, uri);
            }
        }
    }

    public Element buildElement(Element parent, XMLStreamReader reader) 
        throws XMLStreamException
    {
        Element e = null;
        
        int event = reader.getEventType();
        while ( true )
        {
            switch( event )
            {
            case XMLStreamConstants.START_ELEMENT:
                e = createElement(reader);
                
                declareNamespaces(reader, e);
                
                for ( int i = 0; i < reader.getAttributeCount(); i++ )
                {
                    Nodes nodes = createAttribute(reader, i);

                    for (int j = 0; j < nodes.size(); j++)
                    {
                        e.addAttribute((Attribute)nodes.get(j));
                    }
                }
                
                if (parent != null)
                    parent.appendChild(e);

                reader.next();
                buildElement(e, reader);

                if (parent == null)
                    return e;

                break;
            case XMLStreamConstants.END_ELEMENT:
                return e;
            case XMLStreamConstants.CHARACTERS:
                if (parent != null)
                    parent.appendChild(reader.getText());
                
                break;
            case XMLStreamConstants.NAMESPACE:
            case XMLStreamConstants.END_DOCUMENT:
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.START_DOCUMENT:
            case XMLStreamConstants.ATTRIBUTE:
            default:
                break;
            }
            event = reader.next();
        }
    }

    /**
     * Create an Element when the XMLStreamReader is at the START_ELEMENT position.
     * 
     * @param reader
     * @return
     */
    protected Element createElement(XMLStreamReader reader)
    {
        String prefix = reader.getPrefix();
        String qName = reader.getLocalName();
        String uri = reader.getNamespaceURI();
        
        if (uri != null)
        {
            if (prefix != null && prefix.length() > 0)
            {
                qName = prefix + ":" + qName;
            }
            
            return new Element(qName, uri);
        }
        else
        {
            return new Element(qName);
        }
    }

    /**
     * @param reader
     * @param i
     * @return
     */
    private Nodes createAttribute(XMLStreamReader reader, int i)
    {
        String attPrefix = reader.getAttributePrefix(i);
        String attQName = reader.getAttributeLocalName(i);
        String attUri = reader.getAttributeNamespace(i);
        String attValue = reader.getAttributeValue(i);
        
        if (attPrefix != null && attPrefix.length() > 0)
        {
            attQName = attPrefix + ":" + attQName; 
        }
        
        Nodes nodes = factory.makeAttribute(attQName, attUri, attValue, Attribute.Type.UNDECLARED);
        return nodes;
    }
}
