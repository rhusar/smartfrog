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
package org.cddlm.client.console;

import org.apache.axis.message.MessageElement;
import org.apache.axis.types.NCName;
import org.apache.axis.types.URI;
import org.smartfrog.services.cddlm.generated.api.types.OptionMapType;
import org.smartfrog.services.cddlm.generated.api.types.OptionType;
import org.smartfrog.services.cddlm.generated.api.types.PropertyMapType;
import org.smartfrog.services.cddlm.generated.api.types.PropertyTupleType;
import org.smartfrog.services.cddlm.generated.api.types.UnboundedXMLAnyNamespace;

import java.math.BigInteger;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;


/**
 * utility code to make the options stuff settable created Sep 2, 2004 2:48:47
 * PM
 */

public class Options {

    private List /* OptionType */ options = new LinkedList();

    public OptionMapType toOptionMap() {
        final OptionMapType map = new OptionMapType();
        OptionType[] optionArray = new OptionType[options.size()];
        map.setOption((OptionType[]) options.toArray(optionArray));
        return map;
    }

    public void addOption(URI name, String value, boolean mustUnderstand) {
        OptionType option = createNamedOption(name, mustUnderstand);
        option.setString(value);
    }

    public void addOption(URI name, boolean value, boolean mustUnderstand) {
        createNamedOption(name, mustUnderstand).set_boolean(value);
    }

    public void addOption(URI name, long value, boolean mustUnderstand) {
        BigInteger v = BigInteger.valueOf(value);
        createNamedOption(name, mustUnderstand).setInteger(v);
    }

    public void addOption(URI name, MessageElement[] xml,
            boolean mustUnderstand) {
        UnboundedXMLAnyNamespace value = new UnboundedXMLAnyNamespace(xml);
        createNamedOption(name, mustUnderstand).setData(value);
    }

    /**
     * remove all options with that name
     *
     * @param name
     */
    public void removeOption(URI name) {
        assert name != null;
        Iterator it = options.listIterator();
        while (it.hasNext()) {
            OptionType optionType = (OptionType) it.next();
            if (name.equals(optionType.getName())) {
                it.remove();
            }
        }
    }

    /**
     * look up an option by name
     *
     * @param name
     * @return the option or null for no match
     */
    public OptionType lookupOption(URI name) {
        assert name != null;
        Iterator it = options.listIterator();
        while (it.hasNext()) {
            OptionType optionType = (OptionType) it.next();
            if (name.equals(optionType.getName())) {
                return optionType;
            }
        }
        return null;
    }

    public Iterator iterator() {
        return options.iterator();
    }

    /**
     * create a named option, add it to the list
     *
     * @param name
     * @param mustUnderstand
     * @return the option
     */
    public OptionType createNamedOption(URI name, boolean mustUnderstand) {
        OptionType option = new OptionType();
        option.setName(name);
        option.setMustUnderstand(Boolean.valueOf(mustUnderstand));
        addOption(option);
        return option;
    }

    public void addOption(OptionType option) {
        options.add(option);
    }

    /**
     * convert a properties structure into a propertymap
     * @param name
     * @param properties
     * @param mustUnderstand
     */
/*    public void addOption(URI name, Properties properties, boolean mustUnderstand) {
        PropertyMapType map=fromProperties(properties);

    }*/

    /**
     * turn a property set into a propertymap
     *
     * @param properties
     * @return
     */
    public static PropertyMapType fromProperties(Properties properties) {
        PropertyTupleType[] tuples = new PropertyTupleType[properties.size()];
        int count = 0;
        Iterator keys = properties.keySet().iterator();
        while (keys.hasNext()) {
            String key = (String) keys.next();
            String value = properties.getProperty(key);
            PropertyTupleType tuple = new PropertyTupleType(new NCName(key),
                    value);
            tuples[count++] = tuple;
        }
        PropertyMapType map = new PropertyMapType(tuples);
        return map;
    }
}
