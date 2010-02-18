package org.smartfrog.sfcore.logging;


import java.io.*;

/**
 *  This interface is used to determine who called us. The implementation does
 *  not have to be thread-safe.
 *  http://www.javaspecialists.co.za/
 */
public interface CallDetective {

   /**
    *  This class is used to determine who called us. It is deliberately not
    *  thread-safe.
    */
   class CallDetective1_4 implements CallDetective {
      private final Throwable tracer = new Throwable();


      /**
       *  Description of the Method
       *
       *@param  depth  Description of the Parameter
       *@return        Description of the Return Value
       */
      public String findCaller(int depth) {
         try {
             if (depth<0) {
                 throw new IllegalArgumentException();
             }
             tracer.fillInStackTrace();
             StackTraceElement[] stack = tracer.getStackTrace();
             if ((depth+1) >= stack.length)
                return tracer.getStackTrace()[stack.length-1].toString();
             else
                return tracer.getStackTrace()[depth+1].toString();
         } catch (Throwable thr){
            return "";
         }
      }
   }


   /**
    *  This is a pre-JDK 1.4 version of the CallDetective. See TJSN 4th edition.
    *  http://www.javaspecialists.co.za/archive/Issue004.html
    */
   class CallDetective1_3 implements CallDetective {
      private final Throwable tracer = new Throwable();
      private final StringWriter sw = new StringWriter(1024);
      private final PrintWriter out = new PrintWriter(sw, false);


      /**
       *  Description of the Method
       *
       *@param  depth  Description of the Parameter
       *@return        Description of the Return Value
       */
      public String findCaller(int depth) {
         if (depth < 0) {
            throw new IllegalArgumentException();
         }

         int lineOfInterest = depth + 3;
         sw.getBuffer().setLength(0);
         // set the buffer back to zero
         tracer.fillInStackTrace();
         tracer.printStackTrace(out);

         LineNumberReader in = new LineNumberReader(
               new StringReader(sw.toString()));

         try {
            String result;
            while ((result = in.readLine()) != null) {
               if (in.getLineNumber() == lineOfInterest) {
                  return beautify(result);
               }
            }
         } catch (IOException ex) {
            // this should REALLY never happen
            throw new RuntimeException(ex.toString());
         }
         throw new IllegalArgumentException();
      }


      /**
       *  Description of the Method
       *
       *@param  raw  Description of the Parameter
       *@return      Description of the Return Value
       */
      private static String beautify(String raw) {
         raw = raw.trim();
         if (raw.startsWith("at ")) {
            return raw.substring(3);
         }
         return raw;
      }
   }


   /**
    *  Returns a String representation of who called us, going back depth
    *  levels.
    *  Example:
    *     Declaration:
    *       private static CallDetective detective = CallDetective.Factory.makeCallDetective();
    *       String caller = "";
    *     Use in code:
    *       caller=detective.findCaller(5);
    *
    *@param  depth  must be greater than 0 and may not exceed the call stack
    *      depth.
    *@return        Description of the Return Value
    */
   public String findCaller(int depth);


   /**
    *  Description of the Class
    */
   public class Factory {
      /**
       *  Description of the Method
       *
       *@return    Description of the Return Value
       */
      public static CallDetective makeCallDetective() {
         if ("1.4".compareTo(System.getProperty("java.version")) > 0) {
            return new CallDetective1_3();
         } else {
            return new CallDetective1_4();
         }
      }
   }
}

