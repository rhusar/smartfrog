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


package org.smartfrog.sfcore.common;

import java.text.MessageFormat;
import java.util.ResourceBundle;


/**
 * Utiliy class to format the messages from the error message bundle.
 *
 */
public final class MessageUtil {
    /**
     * Default Message Bundle Name.
     */
    private static final String MESSAGE_BUNDLE = "org.smartfrog.sfcore.common.ErrorMessageBundle";
    /**
     * Resource Bundle holding the messages.
     */
    private static final ResourceBundle myResources = ResourceBundle.getBundle(MESSAGE_BUNDLE);

    /**
     * empty constructor
     */
    private MessageUtil(){
    }

    /**
     * Gets the message string for the message key.
     * @param messageKey the message key
     * @return Message String
     */
    private static String getMessageString(String messageKey) {
        return myResources.getString(messageKey);
    }

    /**
     * Returns the formatted string for the message key.
     *
     * @param messageKey message code
     *
     * @return Formatted message after replacing the arguments in the message
     */
    public static String formatMessage(String messageKey) {
        MessageFormat mf = new MessageFormat(getMessageString(messageKey));

        return mf.format(new Object[0]);
    }

    /**
     * Returns the formatted string for the message key.
     *
     * @param messageKey message code
     * @param arg0 argument
     *
     * @return Formatted message after replacing the arguments in the message
     */
    public static String formatMessage(String messageKey, Object arg0) {
        MessageFormat mf = new MessageFormat(getMessageString(messageKey));
        Object[] args = new Object[1];
        args[0] = arg0;

        return mf.format(args);
    }

    /**
     * Returns the formatted string for the message key.
     *
     * @param messageKey message code
     * @param arg0 argument
     * @param arg1 argument
     *
     * @return Formatted message after replacing the arguments in the message
     */
    public static String formatMessage(String messageKey, Object arg0,
        Object arg1) {
        MessageFormat mf = new MessageFormat(getMessageString(messageKey));
        Object[] args = new Object[2];
        args[0] = arg0;
        args[1] = arg1;

        return mf.format(args);
    }

    /**
     * Returns the formatted string for the message key.
     *
     * @param messageKey message code
     * @param arg0 argument
     * @param arg1 argument
     * @param arg2 argument
     *
     * @return Formatted message after replacing the arguments in the message
     */
    public static String formatMessage(String messageKey, Object arg0,
        Object arg1, Object arg2) {
        MessageFormat mf = new MessageFormat(getMessageString(messageKey));
        Object[] args = new Object[3];
        args[0] = arg0;
        args[1] = arg1;
        args[2] = arg2;

        return mf.format(args);
    }

    // Include implementations of formatMessage() for as many arguments
    // as you need
}
