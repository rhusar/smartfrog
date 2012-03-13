package org.smartfrog.sfcore.logging;


/**
 *  This class is used to determine who called us. It is deliberately not
 *  thread-safe.
 *  http://www.javaspecialists.co.za/
 */
public class CallDetective {

    private final Throwable tracer = new Throwable();

    public CallDetective() {
    }

    /**
     *  Returns a String representation of who called us, going back depth
     *  levels.
     *  Example:
     *     Declaration:
     *       private static CallDetective detective = new CallDetective();
     *       String caller = "";
     *     Use in code:
     *       caller=detective.findCaller(5);
     *
     *@param  depth  must be greater than 0 and may not exceed the call stack
     *      depth.
     *@return Description of the Return Value
     */
    public String findCaller(int depth) {
        try {
            if (depth < 0) {
                throw new IllegalArgumentException();
            }
            tracer.fillInStackTrace();
            StackTraceElement[] stack = tracer.getStackTrace();
            if ((depth + 1) >= stack.length) {
                return tracer.getStackTrace()[stack.length - 1].toString();
            } else {
                return tracer.getStackTrace()[depth + 1].toString();
            }
        } catch (Throwable thr) {
            return "";
        }
    }

}

