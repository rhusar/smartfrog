package org.smartfrog.sfcore.common;

import java.io.Serializable;

/**
 * Class that implements the binary array data for the SmartFrog  language.
 * It is immutable, and accessing the byte array returns a copy, so the data cannot be changed
 */
public class SFByteArray implements Serializable {
    private byte[] array;

    /**
     * Constructor for an instance of the class for use by the parser - includes line and character info
     * for error messages.
     * @param data the string representing the hexadecimal data
     * @param line the line number in the parsed stream
     * @param column the colummn in the parsed stream
     *
     * @throws SmartFrogParseException if the data cannot be converted
     */
    public SFByteArray(String data, int line, int column) throws SmartFrogParseException {
        if (data.length() != ((data.length() >> 1) << 1))
            throw new SmartFrogParseException(new StringBuffer()
                    .append("HEX data at line:").append(line)
                    .append(" column:").append(column)
                    .append(" must have even number of characters")
                    .toString());
        processString(data);
    }

    /**
     * Constructor for an instance of the class from a byte array - clones the data
     * to ensure immutability
     * @param array the data
     */
    public SFByteArray(byte[] array) {
        this.array = (byte[])(array.clone());
    }

    /**
     * Return a string in canonical form representing the data
     */
    public String toString() {
        return new StringBuffer().append("#HEX#").append(processArray()).append("#").toString();
    }

    /**
     * return a copy of the internal array of data
     */
    public byte[] byteArray() {
        return (byte[])array.clone();
    }

    private int mapChar(char c) {
        byte res = 0;
        switch (c) {
            case '0': res = 0; break;
            case '1': res = 1; break;
            case '2': res = 2; break;
            case '3': res = 3; break;
            case '4': res = 4; break;
            case '5': res = 5; break;
            case '6': res = 6; break;
            case '7': res = 7; break;
            case '8': res = 8; break;
            case '9': res = 9; break;
            case 'a': res = 10; break;
            case 'b': res = 11; break;
            case 'c': res = 12; break;
            case 'd': res = 13; break;
            case 'e': res = 14; break;
            case 'f': res = 15; break;
            case 'A': res = 10; break;
            case 'B': res = 11; break;
            case 'C': res = 12; break;
            case 'D': res = 13; break;
            case 'E': res = 14; break;
            case 'F': res = 15; break;
        }
        return res;
    }

    private void processString(String s) {
        array = new byte[s.length()/2] ;
        for (int i = 0; i < s.length(); i=i+2) {
            int b1 = (mapChar(s.charAt(i)) << 4);
            int b2 = mapChar(s.charAt(i + 1));
            int index = i >> 1;
            int b = (b1 + b2);
            array[index] = (byte) b;  // note caste down may become negative, overflow impossible
        }
    }

    private static char[] indexstring = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};
    private String processArray() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < array.length; i++) {
            int j = (array[i] < 0)? 256 + array[i] : array[i] ;  // deal with caste-up sign-extending!
            int k = (j >> 4);
            int l = (j - (k << 4));
            s.append(indexstring[k]);
            s.append(indexstring[l]);
        }
        return s.toString();
    }
}
