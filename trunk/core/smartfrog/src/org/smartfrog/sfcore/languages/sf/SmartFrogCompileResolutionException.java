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

//package org.smartfrog.sfcore.common;
package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogException;
import org.smartfrog.sfcore.reference.Reference;

/**
  * A SmartFrogCompileResolutionException is thrown when an irrecoverable
  * resolution error occurs while compilation.
  *
  *  A resolution exception contains a reason
  *  code and a name (reference) of the component where the resolution failed.
  *  Possible failure conditions and reason codes are given in the table below.
  *
  *  <tablecellpadding=0 cellspacing=0 border=1>
  *
  *    <tr>
  *
  *      <tdalign="left" width="50%" >
  *        Placement resolution failed
  *      </td>
  *
  *      <tdalign="left" width="50%" >
  *        placeResolution
  *      </td>
  *
  *    </tr>
  *
  *    <tr>
  *
  *      <tdalign="left" width="50%" >
  *        Type resolution failed
  *      </td>
  *
  *      <tdalign="left" width="50%" >
  *        typeResolution
  *      </td>
  *
  *    </tr>
  *
  *    <tr>
  *
  *      <tdalign="left" width="50%" >
  *        Deployment resolution failed
  *      </td>
  *
  *      <tdalign="left" width="50%" >
  *        deployResolution
  *      </td>
  *
  *    </tr>
  *
  *  </table>
  *
 */
public class SmartFrogCompileResolutionException extends
                                               SmartFrogCompilationException {
    /** String name for resolution phase. */
    public final static String RESOLUTION_PHASE = "resolutionPhase";

    /** String name for type resolution phase. */
    public final static String TYPE_PHASE ="type";
    /** String name for place resolution phase. */
    public final static String PLACE_PHASE ="place";
    /** String name for sfconfig resolution phase. */
    public final static String SFCONFIG_PHASE ="sfconfig";
    /** String name for print resolution phase. */
    public final static String PRINT_PHASE ="print";
    /** String name for link resolution phase. */
    public final static String LINK_PHASE ="link";
    /** String name for function resolution phase. */
    public final static String FUNCTION_PHASE ="function";


    /**
     * Constructs a SmartFrogCompileResolutionException with specified message.
     *
     * @param message exception message
     */
    public SmartFrogCompileResolutionException(String message) {
        super(message);
    }


    /**
     *  Constructs a resolution exception with additional data.
     *
     *@param  message exception message
     *@param  cause  exception causing this exception
     *@param  source source that raised the exception
     *@param resolutionPhase Resolution phase that caused the exception
     *@param  data    additional data for exception
     */
    public SmartFrogCompileResolutionException (String message, Throwable cause, Reference source, String resolutionPhase, Object data) {
       super(message, cause);
       if ((source!=null)) put(SOURCE,source);
       if ((resolutionPhase!=null))put(RESOLUTION_PHASE,resolutionPhase);
       if (data!=null) put(DATA,data);
    }


    /**
     * Constructs a SmartFrogCompileResolutionException with specified cause.
     *
     * @param cause exception causing this exception
     */
    public SmartFrogCompileResolutionException(Throwable cause) {
        super(cause);
    }

    /**
     * Constructs a SmartFrogCompileResolutionException with specified cause.
     * Also initializes the exception context with component details.
     *
     * @param cause exception causing this exception
     * @param resolutionPhase Resolution phase that caused the exception
     */
    public SmartFrogCompileResolutionException(Throwable cause, String resolutionPhase) {
        this(null,cause,null,resolutionPhase,null);
    }

    /**
     * Constructs a SmartFrogCompileResolutionException with specified message
     * and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     */
    public SmartFrogCompileResolutionException(String message,
                                                            Throwable cause) {
        super(message, cause);
    }

    /**
     * Constructs a SmartFrogCompileResolutionException with specified message
     * and cause.
     *
     * @param message exception message
     * @param cause exception causing this exception
     * @param resolutionPhase Resolution phase that caused the exception
     */
    public SmartFrogCompileResolutionException(String message,
                                                            Throwable cause, String resolutionPhase) {
        this(message,cause,null,resolutionPhase,null);
    }


    /**
     *  Creates a placement resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException placeResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, PLACE_PHASE, data);//   (source, "placeResolution", data);
    }

    /**
     *  Creates a type resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements.
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException typeResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, TYPE_PHASE, data);//   (source, "placeResolution", data);
    }



    /**
     *  Creates a link resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements.
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException linkResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, LINK_PHASE, data);//   (source, "placeResolution", data);
    }


    /**
     *  Creates a function resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements.
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException functionResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, FUNCTION_PHASE, data);//   (source, "placeResolution", data);
    }

    /**
     *  Creates a function resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements.
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException sfConfigResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, SFCONFIG_PHASE, data);//   (source, "placeResolution", data);
    }

    /**
     *  Creates a function resolution exception. The data field generally is a
     *  vector containing the unresolved references.
     *
     * @param reason message exception
     * @param  source  component causing the exception
     * @param  data    vector of unresolved placements.
     * @param cause exception causing this exception
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogCompileResolutionException printResolution(String reason, Reference source, Object data, Throwable cause) {
       return new SmartFrogCompileResolutionException (reason, cause, source, PRINT_PHASE, data);//   (source, "placeResolution", data);
    }


    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogException forward (Throwable thr){
        if (thr instanceof SmartFrogCompileResolutionException) {
            return (SmartFrogCompileResolutionException)thr;
        } else {
            return new SmartFrogCompileResolutionException (thr);
        }
    }

    /**
     * To forward SmartFrog exceptions instead of chain them.
     *
     * @param thr throwable object to be forwarded
     * @param resolutionPhase Resolution phase that caused the exception to be
     * forwarded
     *
     * @return SmartFrogException that is a SmartFrogCompileResolutionException
     */
    public static SmartFrogException forward (Throwable thr, String resolutionPhase){
        if (thr instanceof SmartFrogCompileResolutionException) {
            if (!(((SmartFrogCompileResolutionException)thr).containsKey(RESOLUTION_PHASE))){
                ((SmartFrogCompileResolutionException)thr).put(RESOLUTION_PHASE,resolutionPhase);
            }
            return (SmartFrogCompileResolutionException)thr;
        } else {
            return new SmartFrogCompileResolutionException (thr,resolutionPhase);
        }
    }

    public String toString(String nm){
        StringBuffer strb = new StringBuffer();
        strb.append(super.toString(nm));
//        strb.append ((((this.containsKey(SOURCE)
//                               && (this.get(SOURCE)!=null)
//                               &&(((Reference)this.get(SOURCE)).size()!=0)))
//                        ? (nm+SOURCE+  ": " + get(SOURCE)) : "" ));
        strb.append ((((this.containsKey(RESOLUTION_PHASE)))
                    ? (nm+RESOLUTION_PHASE+  ": " + get(RESOLUTION_PHASE)) : ""));
        return strb.toString();

    }

}
