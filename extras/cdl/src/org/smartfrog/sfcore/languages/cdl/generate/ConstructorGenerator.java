/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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
package org.smartfrog.sfcore.languages.cdl.generate;

import org.smartfrog.sfcore.languages.cdl.dom.PropertyList;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

/**
 * created 02-Feb-2006 12:45:34
 */

public class ConstructorGenerator extends BaseGenerator {

    private Class clazz;
    private Constructor constructor;

    public ConstructorGenerator(Class clazz, boolean trim) throws SmartFrogException {
        super(trim);
        assert clazz!=null;
        bind(clazz);
    }



    public ConstructorGenerator(String classname, boolean trim) throws SmartFrogException {
        super(trim);
        try {
            Class clazz=Class.forName(classname);
            bind(clazz);
        } catch (ClassNotFoundException e) {
            throw SmartFrogException.forward(e);
        }
    }

    private void bind(Class clazz) throws SmartFrogException {
        this.clazz = clazz;
        try {
            constructor = clazz.getConstructor(String.class);
        } catch (NoSuchMethodException e) {
            throw SmartFrogException.forward("creating a constructor for "+clazz,e);
        }
    }

    /**
     * Generate a type from a string value
     *
     * @param node to work with
     * @return
     * @throws org.smartfrog.sfcore.common.SmartFrogException
     *
     */
    public Object generateType(PropertyList node) throws SmartFrogException {
        String text=extractStringValue(node);
        try {
            final Object instance = constructor.newInstance(text);
            return instance;
        } catch (InstantiationException e) {
            throw SmartFrogException.forward("creating an instance of " + clazz, e);
        } catch (IllegalAccessException e) {
            throw SmartFrogException.forward("creating an instance of " + clazz, e);
        } catch (InvocationTargetException e) {
            throw SmartFrogException.forward("creating an instance of " + clazz, e);
        }
    }

}
