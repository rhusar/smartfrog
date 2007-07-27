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
package org.smartfrog.projects.alpine.handlers;

import org.smartfrog.projects.alpine.core.EndpointContext;
import org.smartfrog.projects.alpine.faults.AlpineRuntimeException;
import org.smartfrog.projects.alpine.interfaces.MessageHandler;
import org.smartfrog.projects.alpine.interfaces.MessageHandlerFactory;

/**
 * Given a classname and optionally, a classloader, demand-create an instance of the handler
 * on use
 * created 02-May-2006 13:28:01
 */

public class InstanceHandlerFactory implements MessageHandlerFactory {

    private ClassLoader classloader;
    private String classname;

    public InstanceHandlerFactory(ClassLoader classloader, String classname) {
        this.classloader = classloader;
        this.classname = classname;
    }

    public InstanceHandlerFactory(Object parent, String classname) {
        this.classloader = parent.getClass().getClassLoader();
        this.classname = classname;
    }


    /**
     * Create a new handler for this context, or return an instance of an existing one. In the latter's case,
     * the handler must be thread-safe.
     *
     * @param context endpoint for which the handler is being created
     * @return the handler
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public MessageHandler createHandler(EndpointContext context) {
        return createNewHandler(classloader, classname, context);
    }

    /**
     * Create a new handler for this context, or return an instance of an existing one. In the latter's case,
     * the handler must be thread-safe.
     *
     * @param classloader
     * @param classname
     * @param context     endpoint for which the handler is being created
     * @return the handler
     * @throws org.smartfrog.projects.alpine.faults.AlpineRuntimeException
     *
     */
    public static MessageHandler createNewHandler(ClassLoader classloader, String classname, EndpointContext context) {
        try {
            Class<MessageHandler> aClass = (Class<MessageHandler>) classloader.loadClass(classname);
            MessageHandler handler = aClass.newInstance();
            return handler;
        } catch (ClassNotFoundException e) {
            throw new AlpineRuntimeException(e);
        } catch (InstantiationException e) {
            throw new AlpineRuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new AlpineRuntimeException(e);
        }
    }
}
