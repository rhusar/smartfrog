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

package org.smartfrog.sfcore.languages.sf.functions;

import java.io.BufferedReader;
import java.io.InputStreamReader;

/**
 * Defines the UserInput function that asks the user for an input on the command
 * line. It returns the value entered.
 */
public class UserInput extends BaseFunction {
    /**
     * Takes user input.
     * @return The value entered by user.
     */
    protected Object doFunction() {
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        String result = "";
        String def = "";
        String prompt = " Enter value for attribute '" +
            //@todo review keyFor in Context.
            /* component.sfParent().sfAttributeKeyFor(component) + */ "' : ";

        if (context.containsKey("prompt")) {
            try {
                prompt = (String) context.get("prompt");
            } catch (ClassCastException e) {
            }
        }

        if (context.containsKey("default")) {
            try {
                def = (String) context.get("default");
                prompt += (" (" + def + ")");
            } catch (ClassCastException e) {
            }
        }

        try {
            System.out.print(prompt);
            result = br.readLine();

            if (result.equals("")) {
                result = def;
            }
        } catch (Exception e) {
        }

        return result;
    }
}
