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

package org.smartfrog.sfcore.logging;

/**
 * A simple interface to modify logging attributes for all subset of loggers
 * when possible.
 *
 */
public interface LogAttributes {


    // ----------- Logging Properties

    /**
     * <p> To change debug logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setDebug(boolean status);


    /**
     * <p> To change error logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setError(boolean status);


    /**
     * <p> To change fatal logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setFatal(boolean status);


    /**
     * <p> To change info logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setInfo(boolean status);


    /**
     * <p> To change trace logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setTrace(boolean status);

    /**
     * <p> To change warn logging status </p>
     *
     * @param status new status value
     * @return previous status value
     */
    public boolean setWarn(boolean status);


    /**
     * <p> To change a property in all registered loggers </p>
     * @param name attribute name
     * @param value new status value
     * @return if it was sucessfull applaying this attribute to any of the registered loggers
     */
    public boolean setAttribute (Object name, Object value);


}
