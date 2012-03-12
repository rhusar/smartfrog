/** (C) Copyright 1998-2008 Hewlett-Packard Development Company, LP

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

import org.smartfrog.sfcore.componentdescription.ComponentDescription;
import org.smartfrog.sfcore.languages.sf.constraints.CoreSolver;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;


/**
 * Implements the context interface. This implementation relies on the
 * OrderedHashtable class in the Utilities, but another class can be used. The
 * important thing for any implementation is the fact that the order in which
 * entries are added to the context should be maintained even through the
 * enumeration returning methods.
 *
 */
public class ContextImpl extends OrderedHashtable implements Context, Serializable, PrettyPrinting, Copying {


    private static final Collection<Character> specialChars = new HashSet<Character>();

    static {
        specialChars.add('.');
        specialChars.add('_');
        specialChars.add('-');
        specialChars.add('+');
        specialChars.add('@');
        specialChars.add('#');
        specialChars.add('~');
        specialChars.add('$');
        specialChars.add('%');
        specialChars.add('^');
        specialChars.add('&');
    }

    private static final Collection<Character> otherChars = new HashSet<Character>(25);

    static {
        otherChars.add(' ');
        otherChars.add('!');
        otherChars.add('\'');
        otherChars.add('*');
        otherChars.add('(');
        otherChars.add(')');
        otherChars.add('[');
        otherChars.add(']');
        otherChars.add('{');
        otherChars.add('}');
        otherChars.add(':');
        otherChars.add(';');
        otherChars.add(',');
        otherChars.add('<');
        otherChars.add('>');
        otherChars.add('?');
        otherChars.add('/');
        otherChars.add('|');
        otherChars.add('`');
        otherChars.add('=');
    }


    private static final Collection<String> reservedWords = new HashSet<String>(30);

    static {
        reservedWords.add("APPLY");
        reservedWords.add("ASSERT");
        reservedWords.add("DATA");
        reservedWords.add("extends");
        reservedWords.add("false");
        reservedWords.add("HERE");
        reservedWords.add("HOST");
        reservedWords.add("PROPERTY");
        reservedWords.add("IPROPERTY");
        reservedWords.add("ENVPROPERTY");
        reservedWords.add("IENVPROPERTY");
        reservedWords.add("CONSTANT");
        reservedWords.add("LAZY");
        reservedWords.add("NULL");
        reservedWords.add("OPTIONAL");
        reservedWords.add("PARENT");
        reservedWords.add("PROCESS");
        reservedWords.add("ROOT");
        reservedWords.add("TBD");
        reservedWords.add("THIS");
        reservedWords.add("true");
        reservedWords.add("IF");
        reservedWords.add("THEN");
        reservedWords.add("ELSE");
        reservedWords.add("FI");
        reservedWords.add("SWITCH");
        reservedWords.add("ENDSWITCH");
        reservedWords.add("VAR");
    }

    private static final Set NULL_TAG_SET = ReadOnlySetWrapper.wrap(null);
    protected Map<Object, Set> attributeTags = new HashMap<Object, Set>(OrderedHashtable.initCap, OrderedHashtable.loadFac);
    protected Map<Object, Set> attributeTagsWrappers = new HashMap<Object, Set>(OrderedHashtable.initCap, OrderedHashtable.loadFac);

    /**
     * Creates an empty context with default capacity.
     */
    public ContextImpl() {
    }

    /**
     * Constructs a context with initial capacity and a load trigger for
     * expansion.
     *
     * @param cap initial capacity
     * @param load load capacity trigger
     */
    public ContextImpl(int cap, float load) {
        super(cap, load);
    }

    /**
     * Returns the first key for which the value is the given one (==).
     * @param value value to look up
     *
     * @return key for value or null if not found
     * @deprecated replaced by sfAttributeKeyFor(value);
     */
    @Override
    public Object keyFor(Object value) {
        return sfAttributeKeyFor(value);
    }

    /**
     * Returns the first attribute which has a particular "equal" value  in the table.
     *
     * @param value value to find in table
     *
     * @return attibute object for value or null if none
     */
    @Override
    public Object sfAttributeKeyForEqual(Object value) {
        if (!contains(value)) {
            return null;
        }
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object theKey = e.nextElement();

            if (get(theKey).equals(value)) {
                return theKey;
            }
        }
        return null;
    }

    /**
     * Returns true if the context contains an equal value.
     * Replaces contains()
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     */
    @Override
    public boolean sfContainsValue(Object value) {
        return containsValue(value);
    }

    /**
     * Returns true if the context contains value reference (==).
     * Replaces contains()
     * @param value object to check
     *
     * @return true if context contains value, false otherwise
     *  @throws NullPointerException  if the value is <code>null</code>.
     */
    @SuppressWarnings("ProhibitedExceptionThrown")
    @Override
    public boolean sfContainsRefValue(Object value) {
        if (value == null) {
            throw new NullPointerException();
        }
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object theKey = e.nextElement();
            if (get(theKey) == (value)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true if the context contains attribute.
     * Replaces containsKey()
     * @param attribute to check
     *
     * @return true if context contains key, false otherwise
     */
    @Override
    public boolean sfContainsAttribute(Object attribute) {
        return containsKey(attribute);
    }


    /**
     * Returns an ordered iterator over the attribute names in the context.
     * The remove operation of this Iterator won't affect
     * the contents of Context
     *
     * @return iterator
     */
    @Override
    public Iterator sfAttributes() {
        return orderedAttributes();

    }

    /**
     * Returns an ordered iterator over the values in the context.
     * The remove operation of this Iterator won't affect
     * the contents of Context

     * @return iterator
     */
    @Override
    public Iterator sfValues() {
        return orderedValues();
    }


    /**
     * Find an attribute in this context.
     *
     * @param name attribute key to resolve
     *
     * @return Object Reference
     *
     * @throws SmartFrogContextException failed to find attribute
     */
    @Override
    public Object sfResolveAttribute(Object name) throws SmartFrogContextException {
        Object result = get(name);
        if (result == null) {
            throw new SmartFrogContextException(
                    MessageUtil.formatMessage(MessageKeys.MSG_NOT_FOUND_ATTRIBUTE, name));
        }
        return result;
    }


    /**
     * Adds an attribute to this context under given name.
     *
     * @param name name of attribute
     * @param value value of attribute
     *
     * @return previous value for name or null if none
     *
     * @throws SmartFrogContextException when name or value are null or name already used
     */
    @Override
    public synchronized Object sfAddAttribute(Object name, Object value)
            throws SmartFrogContextException {
        if ((name == null) || (value == null)) {
            if (name == null) {
                throw new SmartFrogContextException(
                        MessageUtil.formatMessage(MessageKeys.MSG_NULL_DEF_METHOD, "'name'",
                                "sfAddAttribute"));
            }
            if (value == null) {
                throw new SmartFrogContextException(
                        MessageUtil.formatMessage(MessageKeys.MSG_NULL_VALUE_OF_KEY_IN,
                                name,
                                "sfAddAttribute"));
            }

            return null;
        }

        if (containsKey(name)) {
            throw new SmartFrogContextException(
                    MessageUtil.formatMessage(MessageKeys.MSG_REPEATED_ATTRIBUTE, name));

        }

        return put(name, value);
    }

    /**
     * Removes an attribute from this context.
     *
     * @param name of attribute to be removed
     *
     * @return removed attribute value if successful or null if not
     *
     * @throws SmartFrogContextException when name is null
     */
    @Override
    public synchronized Object sfRemoveAttribute(Object name)
            throws SmartFrogContextException {
        if (name == null) {
            throw new SmartFrogContextException(
                    MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                            "sfRemoveAttribute"));
        }
        return remove(name);
    }

    /**
     * Replace named attribute in context. If attribute is not
     * present it is added to the context.
     *
     * @param name of attribute to replace
     * @param value value to add or replace
     *
     * @return the old value if present, null otherwise
     *
     * @throws SmartFrogContextException when name or value are null
     */
    @Override
    public synchronized Object sfReplaceAttribute(Object name, Object value)
            throws SmartFrogContextException {
        if ((name == null) || (value == null)) {
            if (name == null) {
                throw new SmartFrogContextException(
                        MessageUtil.formatMessage(MSG_NULL_DEF_METHOD, "'name'",
                                "sfReplaceAttribute"));
            }
            if (value == null) {
                throw new SmartFrogContextException(
                        MessageUtil.formatMessage(MSG_NULL_VALUE_OF_KEY_IN,
                                name,
                                "sfReplaceAttribute"));
            }

            return null;
        }

        return put(name, value);
    }


    /**
     * Returns the attribute key given a value. Uses == for the comparison
     *
     * @param value value to look up key for
     *
     * @return key for attribute value or null if none
     */

    // perhaps this should be synchronized... but causes problems with sfCompleteName if it is
    @Override
    public Object sfAttributeKeyFor(Object value) {
        if (!contains(value)) {
            return null;
        }
        for (Enumeration e = keys(); e.hasMoreElements();) {
            Object theKey = e.nextElement();

            if (get(theKey) == (value)) {
                return theKey;
            }
        }
        return null;
    }

    private boolean hasAttributeTags() {
        return attributeTags != null;
    }

    /**
     * Get the tag for an attribute -or null if it is not declared
     * @param name the attribute name to look for
     * @return the value or null if there is no such entry
     */
    private Set getAttributeTag(final Object name) {
        return hasAttributeTags() ? attributeTags.get(name) : null;
    }

    /**
     * Remove a tag if there is a local tag map and the key is not null
     * @param key the key to look for
     */
    private void removeTag(final Object key) {
        if (hasAttributeTags() && key != null) {
            attributeTags.remove(key);
            attributeTagsWrappers.remove(key);
        }
    }

    /**
     * Create the tag fields if they are currently null.
     * Unsynchronized.
     */
    private void demandCreateTagFields() {
        if (!hasAttributeTags()) {
            attributeTags = new HashMap<Object, Set>(OrderedHashtable.initCap, OrderedHashtable.loadFac);
            attributeTagsWrappers = new HashMap<Object, Set>(OrderedHashtable.initCap, OrderedHashtable.loadFac);
        }
    }

    /**
     * Clear the tag fields.
     * Unsynchronized.
     */
    private void clearTagFields() {
        attributeTags = null;
        attributeTagsWrappers = null;
    }


    /**
     * Clear the tag fields if they are currently empty
     * Unsynchronized.
     */
    private void demandClearTagFields() {
        if (hasAttributeTags() && attributeTags.isEmpty()) {
            clearTagFields();
        }
    }

    /**
     * Create a tag set; create the tag fields if needed.
     * @param name set name -it is assumed this entry is not already in the hash set
     * @return the new tag set.
     */
    private Set createTagSet(Object name) {
        Set s = Collections.synchronizedSet(new HashSet(OrderedHashtable.initCap, OrderedHashtable.loadFac));
        demandCreateTagFields();
        attributeTags.put(name, s);
        attributeTagsWrappers.put(name, ReadOnlySetWrapper.wrap(s));
        return s;
    }

    /**
     * Delete the tag set. Will reset the tag fields if they are now empty.
     * @param name tag set name.
     */
    private void deleteTagSet(Object name) {
        if (hasAttributeTags()) {
            attributeTags.remove(name);
            attributeTagsWrappers.remove(name);
            demandClearTagFields();
        }
    }

    /**
     * Set the TAGS for an attribute. TAGS are simply uninterpreted strings associated
     * with each attribute.
     *
     * @param name attribute key for tags
     * @param tags a set of tags
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized void sfSetTags(Object name, Set tags) throws SmartFrogContextException {
        if (name == null || !containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exists for setting tags");
        }
        Set s = getAttributeTag(name);
        if (s == null) {
            if (!tags.isEmpty()) {
                s = createTagSet(name);
                s.addAll(tags);
            }
        } else {
            s.clear();
            s.addAll(tags);
            if (s.isEmpty()) {
                deleteTagSet(name);
            }
        }
    }

    /**
     * Get the TAGS for an attribute. TAGS are simply uninterpreted strings associated
     * with each attribute.
     *
     * @param name attribute key for tags
     * @return the set of tags
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized Set sfGetTags(Object name) throws SmartFrogContextException {
        if (name == null || !containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exists for getting tags");
        }
        Set s = null;
        if (hasAttributeTags()) {
            s = attributeTagsWrappers.get(name);
        }
        if (s == null) {
            s = NULL_TAG_SET;
        }
        return s;
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags
     * @param tag  a tag to add to the set
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized void sfAddTag(Object name, String tag) throws SmartFrogContextException {
        if (!containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exists for adding tags");
        }
        if (tag != null) {
            demandCreateTagFields();
            Set s = getAttributeTag(name);
            if (s == null) { // add it
                s = createTagSet(name);
            }
            s.add(tag);
        }
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags
     * @param tag  a tag to remove from the set
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized void sfRemoveTag(Object name, String tag) throws SmartFrogContextException {
        if (!containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exist for removing tags");
        }
        Set s = getAttributeTag(name);
        if (s == null) {
            // do nothing - its not there
        } else {
            s.remove(tag);
            if (s.isEmpty()) {
                deleteTagSet(name);
            }
        }
    }

    /**
     * add a tag to the tag set of an attribute
     *
     * @param name attribute key for tags
     * @param tags  a set of tags to add to the set
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized void sfAddTags(Object name, Set tags) throws SmartFrogContextException {
        if (!containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exist for adding tags");
        }
        if (tags != null && !tags.isEmpty()) {
            Set s = getAttributeTag(name);
            if (s == null) {
                s = createTagSet(name);
            }
            s.addAll(tags);
        }
    }

    /**
     * remove a tag from the tag set of an attribute if it exists
     *
     * @param name attribute key for tags
     * @param tags  a set of tags to remove from the set
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    @Override
    public synchronized void sfRemoveTags(Object name, Set tags) throws SmartFrogContextException {
        if (!containsKey(name)) {
            throw new SmartFrogContextException("Attribute " + name + " does not exists for removing tags");
        }
        Set s = getAttributeTag(name);
        if (s != null) {
            s.removeAll(tags);
            if (s.isEmpty()) {
                deleteTagSet(name);
            }
        }
    }

    /**
     * Return an iterator over the tags for an attribute
     *
     * @param name the name of the attribute
     * @return an iterator over the tags
     * @throws SmartFrogContextException
     *          the attribute does not exist;
     */
    public Iterator sfTags(Object name) throws SmartFrogContextException {
        return sfGetTags(name).iterator();
    }

    /**
     * Return whether or not a tag is in the list of tags for an attribute
     *
     * @param name the name of the attribute
     * @param tag the tag to check
     *
     * @return whether or not the attribute has that tag
     * @throws SmartFrogContextException the attribute does not exist
     */
    @Override
    public boolean sfContainsTag(Object name, String tag) throws SmartFrogContextException {
        if (!containsKey(name)) {
            throw new SmartFrogContextException(
                    "Attribute " + name + " does not exists for validating tag's existance");
        }
        Set s = getAttributeTag(name);
        return (s != null) && s.contains(tag);
    }

    /**
     * Compares the specified Object with this Context Tags for equality
     *
     * @param  o object to be compared for equality with this Context
     * @return true if the specified Object is equal to this Map.
     */
    public synchronized boolean equalsTags(Object o) {
        if (o == attributeTags) {
            //catches the special case of both entries being null (the set of context tags are implicitly equal)
            //as well as the situation in which the references are identical
            return true;
        }

        if (!(o instanceof Map)) {
            return false;
        }

        return hasAttributeTags() && attributeTags.equals(o);
    }

    /**
     * Returns a string representation of the component. This will give a
     * description of the component which is parseable, and deployable
     * again... Unless someone removed attributes which are essential to
     * startup that is. Large description trees should be written out using
     * writeOn since memory for large strings runs out quick!
     *
     * @return string representation of component
     */
    @Override
    public String toString() {
        StringWriter sw = new StringWriter();

        try {
            writeOn(sw);
        } catch (IOException ioex) {
            sw.write("[ERROR] ContextImpl " + ioex.toString());
            // ignore should not happen
        }

        return sw.toString();
    }

    /**
     * Writes this component description on a writer. Used by toString. Should
     * be used instead of toString to write large descriptions to file, since
     * memory can become a problem given the LONG strings created
     *
     * @param ps writer to write on
     *
     * @throws IOException failure while writing
     */
    @Override
    public void writeOn(Writer ps) throws IOException {
        writeContextOn(ps, 0, this.keys());
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
    @Override
    public void writeOn(Writer ps, int indent) throws IOException {
        writeContextOn(ps, indent, this.keys());
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
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = get(key);
            tabPad(ps, indent);
            writeTagsOn(ps, indent, key);
            writeKeyOn(ps, indent, key);
            ps.write(' ');
            writeValueOn(ps, indent, value);
            ps.write('\n');
        }
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
        try {
            Set tagSet = sfGetTags(key);
            if (!tagSet.isEmpty()) {
                ps.write("[ ");
                for (Object tag : tagSet) {
                    ps.write(tag.toString() + " ");
                }
                ps.write("] ");
            }
        } catch (SmartFrogContextException e) {
            // shouldn't happen...
        }
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
        boolean requiresQuotes = false;
        String keyS = key.toString();
        String s = unfixEscapes(keyS);

        for (int i = 0; i < keyS.length(); i++) {
            char c = keyS.charAt(i);
            if (!isLetter(c) && !isDigit(c) && !isSpecial(c)) {
                requiresQuotes = true;
                break;
            }
        }
        requiresQuotes |= (!isLetter(s.charAt(0))) || reservedWords.contains(s);
        if (requiresQuotes) {
            ps.write("\"");
        }
        ps.write(s);
        if (requiresQuotes) {
            ps.write("\"");
        }
    }

    /**
     * Writes a given value on a writer. Recognises descriptions, strings and
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
                ((PrettyPrinting) value).writeOn(ps, indent);
            } catch (IOException ex) {
                throw ex;
            } catch (java.lang.StackOverflowError thr) {
                StringBuffer msg =
                        new StringBuffer("Failed to pretty print value. Possible cause: cyclic reference.");
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
     * Writes a given value on a writer. Recognises descriptions, strings and
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
            ps.write("\"" + unfixEscapes((String) value) + "\"");
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
    private static boolean isLetter(char c) {
        return (
                ((c >= '\u0061') && (c <= '\u007a')) ||
                        ((c >= '\u0041') && (c <= '\u005a')) ||
                        (c == '\u0024') ||
                        (c == '\u005f') ||
                        ((c >= '\u00A1') && (c <= '\u00A5')) ||
                        (c == '\u00AA') ||
                        (c == '\u00B5') ||
                        (c == '\u00BA') ||
                        ((c >= '\u00C0') && (c <= '\u00D6')) ||
                        ((c >= '\u00D8') && (c <= '\u00F6')) ||
                        ((c >= '\u00F8') && (c <= '\u00FF'))
        );
    }

    private static boolean isDigit(char c) {
        return (
                ((c >= '\u0030') && (c <= '\u0039'))
        );
    }

    private static boolean isSpecial(char c) {
        return specialChars.contains(c);
    }

    private static boolean isOther(char c) {
        return otherChars.contains(c);
    }

    private static final char[] toHex =
            {'0', '1', '2', '3', '4', '5', '6', '7',
                    '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    private static String convertToEscape(char c) {
        if (c == '\\') {
            return ("\\\\");
        }
        if (c == '\n') {
            return ("\\n");
        }
        if (c == '\t') {
            return ("\\t");
        }
        if (c == '\b') {
            return ("\\b");
        }
        if (c == '\r') {
            return ("\\r");
        }
        if (c == '\f') {
            return ("\\f");
        }
        if (c == '\"') {
            return ("\\\"");
        }
        int ci = (int) c;
        return "\\u" +
                toHex[(ci & 0xf000) >> 12] +
                toHex[(ci & 0x0f00) >> 8] +
                toHex[(ci & 0x00f0) >> 4] +
                toHex[ci & 0x000f];
    }

    private static String unfixEscapes(String s) {
        StringBuilder sb = new StringBuilder(s);
        int i = 0;
        while (i < sb.length()) {
            char c = sb.charAt(i);
            if (!(isLetter(c) || isDigit(c) || isSpecial(c) || isOther(c))) {
                String x = convertToEscape(c);
                sb.replace(i, i + 1, x);
                i += x.length();
            } else {
                i++;
            }
        }
        return sb.toString();
    }

    /**
     * Gets a given value in its String form. Recognises descriptions, strings and
     * vectors of basic values and turns them into string representation.
     * Default is to turn into string using normal toString() call
     *
     * @param obj Object to be given in String form
     * @return String
     * @throws IOException failure while writing
     */
    public static String getBasicValueFor(Object obj) throws IOException {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        writeBasicValueOn(pw, 0, obj);
        return sw.toString();
    }

    /**
     * Internal method to pad out a writer.
     *
     * @param ps writer to tab to
     * @param amount amount to tab
     *
     * @throws IOException failure while writing
     */
    protected static void tabPad(Writer ps, int amount) throws IOException {
        for (int i = 0; i < amount; i++) {
            ps.write("  ");
        }
    }


    // ///////////////////////////////////////////////////////////////////////////////////
    // reimplementation of the hash table and order hash table methods to deal with the tags
    // ///////////////////////////////////////////////////////////////////////////////////


    /**
     * Clears the tags as well as the hashtable.
     * Overwrites OrderedHashtable.clear().
     */
    @Override
    public void clear() {
        super.clear();
        attributeTags.clear();
        attributeTagsWrappers.clear();
    }

    /**
     * Removes an entry from the hashtable, the attribute tags and wrappers, and any core solver.
     * Overwrites OrderedHashtable.remove().
     *
     * @param key key to remove
     *
     * @return removed object or null if there was no such entry.
     */
    @Override
    public Object remove(Object key) {
        Object r = super.remove(key);
        removeTag(key);
        if (r != null) {
            CoreSolver.getInstance().addUndoPut(this, key, r);
        }
        return r;
    }

    /**
     *   Removes the element at the specified position.
     *   Removes the key (and its corresponding value) from this hashtable.
     *   This method does nothing if the key is not in the hashtable.
     *   Overrides the method in OrderedHashtable
     *
     * @param index index to remove
     *
     * @return the value to which the index had been mapped in this hashtable,
     *         or null if the key did not have a mapping
     */
    public Object remove(int index) {
        Object key = orderedKeys.remove(index);
        remove(key);
        removeTag(key);
        Object r = super.remove(index);
        if (r != null) {
            CoreSolver.getInstance().addUndoPut(this, key, r);
        }
        return r;
    }


    /**
     * Renames an entry in the table, leaving its position in the table
     * unchanged. Overrides method in OrderedHashtable.
     *
     * @param key1 the initial key to be renamed
     * @param key2 the new key name
     *
     * @return the initial key, or null if it wasn't in the table
     */
    @Override
    public Object rename(Object key1, Object key2) {
        // if the same don't do anything
        if (key1.equals(key2)) {
            return key1;
        }

        super.rename(key1, key2);

        // safe because key1 != key2
        if (hasAttributeTags() && attributeTags.containsKey(key1)) {
            attributeTags.put(key2, attributeTags.get(key1));
            attributeTags.remove(key1);
            attributeTagsWrappers.put(key2, attributeTagsWrappers.get(key1));
            attributeTagsWrappers.remove(key1);
        }

        return key1;
    }

    /**
     * Does a shallow copy of the hashtable, the ordered keys and
     * the attributeTags.
     *
     * @return shallow copy of this table
     */

    @Override
    public Object clone() {
        Object ret = super.clone();
        if (hasAttributeTags()) {
            Map m = new HashMap(attributeTags.size(), OrderedHashtable.loadFac);
            Map w = new HashMap(attributeTags.size(), OrderedHashtable.loadFac);
            for (Object key : attributeTags.keySet()) {
                Set s = getAttributeTag(key);
                Set sc = Collections.synchronizedSet(new HashSet(s.size(), OrderedHashtable.loadFac));
                sc.addAll(s);
                m.put(key, sc);
                w.put(key, ReadOnlySetWrapper.wrap(sc));
            }
            ((ContextImpl) ret).attributeTags = m;
            ((ContextImpl) ret).attributeTagsWrappers = w;
        }
        return ret;
    }

    /**
     * Does a deep copy of the hashtable. Values in the hashtable which
     * understand the Copying interface get copied properly. If the Values
     * cannot be copied, the basic SF values (numbers, strings, booleans, are
     * each properly dealt with. Other values are copied using serialize/deserialize
     * if they implement serialization - note that because of this transient data will
     * not be copied. It also copies the attributeTags.
     *
     * This overrides the one in OrderedHashtable.
     *
     * @return copy of hashtable
     */
    @Override

    public Object copy() {
        // note that since the super method uses clone,
        // this is already copying attributeTags if they are present
        return super.copy();
    }


    /**
     * Compares the specified Object with this Context for equality,
     * as per the definition in the Map interface.
     *
     * @param  o object to be compared for equality with this Context
     * @return true if the specified Object is equal to this Map.
     */
    @Override

    public synchronized boolean equals(Object o) {

        if (o == this) {
            return true;
        }

        if (!(o instanceof Context)) {
            return false;
        }

        // Compares HashMap
        if (!super.equals(o)) {
            return false;
        }


        // Compares Tags
        return (o instanceof ContextImpl) && (((ContextImpl) o).equalsTags(attributeTags));
    }

    /**
     * Returns the hash code value for this Context
     *
     */
    @Override
    public synchronized int hashCode() {
        // Simple hashcode using Joshua Bloch's recommendation
        int result = 17;
        if (hasAttributeTags()) {
            result = 37 * result + attributeTags.hashCode();
        }
        result = 37 * result + super.hashCode();
        return result;
    }

    /**
     * Add an object to the hashtable.
     *
     * @param key key for association
     * @param value value for hashtable
     *
     * @return previous value for key or null if none
     */
    @Override
    public Object put(Object key, Object value) {
        Object oldValue = super.put(key, value);

        CoreSolver.getInstance().addUndoPut(this, key, oldValue);
        return oldValue;
    }


    /**
     * Sets originating description for context.  Used in constraint solving.
     * @param originatingDescr originating ComponentDescription for context 
     */
    @Override
    public void setOriginatingDescr(ComponentDescription originatingDescr) {
        this.originatingDescr = originatingDescr;
        ((ContextImpl) originatingDescr.sfContext()).originatingDescr = originatingDescr;
    }

    /**
     * Gets the originating description for context.  Used in constraint solving.
     * @return ComponentDescription originating description
     */
    @Override
    public ComponentDescription getOriginatingDescr() {
        return originatingDescr;
    }

    /** The originating component description of this context. */
    private ComponentDescription originatingDescr;

    /**
     * Verifies that comp is a sub-type of this context, based on context being a prefix of comp in terms of keys
     * @return Whether sub-type
     */
    @Override
    public boolean ofType(ComponentDescription comp) {
        ContextImpl comp_cxt = (ContextImpl) comp.sfContext();
        Iterator comp_iter = comp_cxt.orderedAttributes();
        Iterator my_iter = orderedAttributes();
        while (my_iter.hasNext()) {
            if (!comp_iter.hasNext() //Absence of any attribute in sub to reflect attribute in super  
                    ||
                    //Attribute in sub not in common with super
                    !my_iter.next().equals(comp_iter.next())) {
                return false;
            }
        }
        return true;
    }

    /**
     * Returns the key at index idx in context
     * @return key for given index, or null if index not valid
     */
    @Override
    public Object getKey(int idx) {
        return orderedKeys.get(idx);
    }

    /**
     * Returns the value at index idx in context
     * @return value for given key index, or null if index not valid
     */
    @Override
    public Object getVal(int idx) {
        Object key = getKey(idx);
        if (key != null) {
            return get(key);
        } else {
            return null;
        }
    }

}
