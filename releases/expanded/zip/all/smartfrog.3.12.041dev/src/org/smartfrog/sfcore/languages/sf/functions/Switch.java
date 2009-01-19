package org.smartfrog.sfcore.languages.sf.functions;

import org.smartfrog.sfcore.common.*;
import org.smartfrog.sfcore.languages.sf.sfcomponentdescription.SFComponentDescription;
import org.smartfrog.sfcore.reference.HereReferencePart;
import org.smartfrog.sfcore.reference.Reference;

import java.util.Iterator;

/**
 * Defines the Switch function.
 */
public class Switch extends BaseFunction implements MessageKeys {
   private static final String conditionPrefix = "IF";
   private static final String valuePrefix = "THEN";

   /**
    * The method to implement the functionality of the switch function.
    *
    * @return an Object representing the answer, return SFNull if no case condition is true
    * @throws org.smartfrog.sfcore.common.SmartFrogFunctionResolutionException
    *          if any of the parameters are not there or of the wrong type
    */
   protected Object doFunction() throws SmartFrogFunctionResolutionException {
      try {

         for (Iterator i = context.sfAttributes(); i.hasNext();) {
            String name = i.next().toString();
            if (name.startsWith(conditionPrefix)) {
               String valueName =  valuePrefix + name.substring(2);
               Object conditionObj = context.get(name);
               Object valueObj = context.get(valueName);

               if (conditionObj == null)
                  throw new SmartFrogFunctionResolutionException(
                        MessageUtil.formatMessage(MISSING_PARAMETER, conditionObj),
                        null, new Reference(name), null);
               if (valueObj == null)
                  throw new SmartFrogFunctionResolutionException(
                        MessageUtil.formatMessage(MISSING_PARAMETER, valueObj),
                        null, new Reference(valueName), null);

               if (!(conditionObj instanceof Boolean))
                  throw new SmartFrogFunctionResolutionException(
                        MessageUtil.formatMessage(ILLEGAL_BOOLEAN_PARAMETER),
                        null, new Reference(name), "Attribute read: " + conditionObj + " (" + conditionObj.getClass().toString() + ")");


               if (((Boolean) conditionObj).booleanValue())
                  return valueObj;
            }
         }
         return SFNull.get();

      } catch (SmartFrogFunctionResolutionException e) {
         e.printStackTrace();
         throw e;
      }
   }
}
