package org.smartfrog.sfcore.languages.sf;

import org.smartfrog.sfcore.parser.WriterLanguageUnparser;
import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.common.SmartFrogParseException;
import org.smartfrog.sfcore.common.SmartFrogCompilationException;
import org.smartfrog.sfcore.common.SmartFrogContextException;
import org.smartfrog.sfcore.common.PrettyPrinting;
import org.smartfrog.sfcore.reference.Reference;

import java.io.*;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.Vector;

/**
 * Unparse descriptions to the sf language
 */
public class SFUnparser implements WriterLanguageUnparser {
   /**
    * Unparses component(s) to a writer.
    *
    * @param w    writer to output the description
    * @param data the description to write
    * @throws org.smartfrog.sfcore.common.SmartFrogParseException
    *          error unparsing stream
    */
   public void sfUnparse(Writer w, ComponentDescription data) throws SmartFrogParseException {
   }

   /**
    * Unparses a reference to a writer.
    *
    * @param w   writer to output the description
    * @param ref the reference to write
    * @throws org.smartfrog.sfcore.common.SmartFrogCompilationException
    *          failed to parse reference
    */
   public void sfUnparseReference(Writer w, Reference ref) throws SmartFrogCompilationException {
   }

   /**
    * Unparses any value to a writer
    *
    * @param w     writer to output the description
    * @param value the value to write
    * @throws org.smartfrog.sfcore.common.SmartFrogParseException
    *          failed to parse any value
    */
   public void sfUnparseValue(Writer w, Object value) throws SmartFrogCompilationException {
   }


   /**
    * Writes this component description on a writer. Used by toString. Should
    * be used instead of toString to write large descriptions to file, since
    * memory can become a problem given the LONG strings created
    *
    * @param ps writer to write on
    * @param indent the indent to use for printing offset
    *
    * @throws IOException failure while writing
    */
   public void writeOn(Writer ps, int indent) throws IOException {
      /*(
       ps.write("extends " + (getEager() ? "" : "DATA "));

       if (sfContext.size() > 0) {
           ps.write(" {\n");
           sfContext.writeOn(ps, indent + 1);
           tabPad(ps, indent);
           ps.write('}');
       } else {
           ps.write(';');
       }
       */
   }


   /**
    * Internal method to pad out a writer.
    *
    * @param ps writer to tab to
    * @param amount amount to tab
    *
    * @throws IOException failure while writing
    */
   protected void tabPad(Writer ps, int amount) throws IOException {
       for (int i = 0; i < amount; i++) {
           ps.write("  ");
       }
   }

         /**
     * Writes the context on a writer.
     *
     * @param ps writer to write on
     * @param indent level
     * @param keys enumeration over the keys of the context to write out
     *
     * @throws IOException failure while writing
     */
    protected void writeContextOn(Writer ps, int indent, Enumeration keys) throws IOException {
       /* while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = get(key);
            tabPad(ps, indent);
            writeTagsOn(ps, indent, key);
            writeKeyOn(ps, indent, key);
            ps.write(' ');
            writeValueOn(ps, indent, value);
            ps.write('\n');
        }*/
    }

    /**
     * Writes given attribute key on a writer.
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param key key to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeTagsOn(Writer ps, int indent, Object key) throws IOException {
       /*if (attributeTags.containsKey(key)) {
          try {
             if (sfGetTags(key).size() > 0) {
                ps.write("[ ");
                for (Iterator i = sfTags(key); i.hasNext();) {
                   ps.write(i.next().toString() + " ");
                }
                ps.write("] ");
             }
          } catch (SmartFrogContextException e) {
             // shouldn't happen...
          }
       }*/
    }
   /**
     * Writes given attribute key on a writer.
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param key key to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeKeyOn(Writer ps, int indent, Object key) throws IOException {
        ps.write(key.toString());
    }

    /**
     * Writes a given value on a writer. Recognizes descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param value value to stringify
     *
     * @throws IOException failure while writing
     */
    protected void writeValueOn(Writer ps, int indent, Object value) throws IOException {
        if (value instanceof PrettyPrinting) {
            try {
                ((PrettyPrinting)value).writeOn(ps, indent);
            } catch (IOException ex) {
                throw ex;
            } catch (java.lang.StackOverflowError thr) {
                   StringBuilder msg = new StringBuilder("Failed to pretty print value. Possible cause: cyclic reference.");
                   msg.append("Cause:#<0># ");
                   msg.append(thr.getCause().toString());
                   throw new java.io.IOException(msg.toString());
            }
        } else {
            writeBasicValueOn(ps, indent, value);
            ps.write(';');
        }
    }


    /**
     * Writes a given value on a writer. Recognizes descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param ps writer to write on
     * @param indent indent level
     * @param value value to stringify
     *
     * @throws IOException failure while writing
     */
    protected static void writeBasicValueOn(Writer ps, int indent, Object value) throws IOException {
        if (value instanceof String) {
            ps.write("\"" + unfixEscapes((String)value) + "\"");
        } else if (value instanceof Vector) {
            ps.write("[|");
            for (Enumeration e = ((Vector) value).elements(); e.hasMoreElements();) {
                writeBasicValueOn(ps, indent, e.nextElement());
                if (e.hasMoreElements()) {
                    ps.write(", ");
                }
            }
            ps.write("|]");
        } else if (value instanceof Long) {
            ps.write(value.toString() + 'L');
        } else if (value instanceof Double) {
            ps.write(value.toString() + 'D');
        } else {
            ps.write(value.toString());
        }
    }

    /**
     *  To fix escape characters in String
     * @param s  String to be fixed
     * @return String
     */
    private static String unfixEscapes(String s) {
        s = s.replaceAll("\\\\", "\\\\\\\\");
        s = s.replaceAll("\n", "\\\\n");
        s = s.replaceAll("\t", "\\\\t");
        s = s.replaceAll("\b", "\\\\b");
        s = s.replaceAll("\r", "\\\\r");
        s = s.replaceAll("\f", "\\\\f");
        return s;
    }

    /**
     * Gets a given value in its String form. Recognizes descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param obj Object to be given in String form
     * @return String
     * @throws IOException failure while writing
     */
    public static String getBasicValueFor (Object obj) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        writeBasicValueOn(pw,0,obj);
        return sw.toString();
    }
}
