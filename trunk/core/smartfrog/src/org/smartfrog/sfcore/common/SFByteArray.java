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
     * for error messages. The data is assumed to be hexadecimal
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
        processHEXString(data, line, column);
    }

        /**
     * Constructor for an instance of the class for use by the parser - includes line and character info
     * for error messages.
     * @param data the string representing the hexadecimal data
     * @param type the encoding of the data - "HEX", "DEC", "OCT", "BIN", "B64"  (B64 not yet supported)
     * @param line the line number in the parsed stream
     * @param column the colummn in the parsed stream
     *
     * @throws SmartFrogParseException if the data cannot be converted
     */
    public SFByteArray(String data, String type, int line, int column) throws SmartFrogParseException {
        if (type.equals("HEX")) {
            if (data.length() != ((data.length() >> 1) << 1))
                throw new SmartFrogParseException(new StringBuffer()
                        .append("HEX data at line:").append(line)
                        .append(" column:").append(column)
                        .append(" must have even number of characters")
                        .toString());
            processHEXString(data, line, column);
        } else if (type.equals("DEC")) {
            if ((data.length()%3) != 0)
                throw new SmartFrogParseException(new StringBuffer()
                        .append("DEC data at line:").append(line)
                        .append(" column:").append(column)
                        .append(" must have a number of characters divisible by 3")
                        .toString());
            processDECString(data, line, column);
        } else if (type.equals("OCT")) {
            if ((data.length()%3)!= 0)
                throw new SmartFrogParseException(new StringBuffer()
                        .append("OCT data at line:").append(line)
                        .append(" column:").append(column)
                        .append(" must have a number of characters divisible by 3")
                        .toString());
            processOCTString(data, line, column);
        } else if (type.equals("BIN")) {
             if ((data.length() % 8) != 0)
                throw new SmartFrogParseException(new StringBuffer()
                        .append("BIN data at line:").append(line)
                        .append(" column:").append(column)
                        .append(" must have a number of characters divisible by 8")
                        .toString());
            processBINString(data, line, column);
        } else if (type.equals("B64")) {
            throw new SmartFrogParseException("B64 byte arrays are not yet supported in SmartFrog");
        } else {
            throw new SmartFrogParseException("Illegal byte array type on construction " + type);
        }

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
     * @return String
     */
    public String toString() {
        return new StringBuffer().append("#HEX#").append(processArray()).append("#").toString();
    }

    /**
     * return a copy of the internal array of data
     * @return  internal array of data
     */
    public byte[] byteArray() {
        return (byte[])array.clone();
    }

    /**
     * Maps the characters
     * @param c char
     * @return  int
     */
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

    /**
     * Process the Hex string
     *
     * @param s String
     */
    private void processHEXString(String s, int line, int column) throws SmartFrogParseException {
        array = new byte[s.length() / 2];
        for (int i = 0; i < s.length(); i = i + 2) {
            int b1 = (mapChar(s.charAt(i)) << 4);
            int b2 = mapChar(s.charAt(i + 1));
            int index = i >> 1;
            int b = (b1 + b2);
            if (b > 255) throw new SmartFrogParseException("Byte data too large:  " + b +
                    " at byte " + (i/2) +
                    ". Data at line: "+ line +
                    " column:"+ column);

            array[index] = (byte) b;  // note caste down may become negative, overflow impossible
        }
    }

    /**
     * Process the Dec string
     *
     * @param s String
     */
    private void processDECString(String s, int line, int column) throws SmartFrogParseException {
        array = new byte[s.length() / 3];
        for (int i = 0; i < s.length(); i = i + 3) {
            int b1 = (mapChar(s.charAt(i)) * 100);
            int b2 = (mapChar(s.charAt(i + 1)) * 10);
            int b3 = mapChar(s.charAt(i + 2));
            int index = (i + 1) / 3;
            int b = (b1 + b2 + b3);
            if (b > 255) throw new SmartFrogParseException("Byte data too large:  " + b +
                    " at byte " + (i/3) +
                    ". Data at line: "+ line +
                    " column:"+ column);
            array[index] = (byte) b;  // note caste down may become negative, overflow impossible
        }
    }

    /**
     * Process the Oct string
     *
     * @param s String
     */
    private void processOCTString(String s, int line, int column) throws SmartFrogParseException {
        array = new byte[s.length() / 3];
        for (int i = 0; i < s.length(); i = i + 3) {
            int b1 = (mapChar(s.charAt(i)) * 64);
            int b2 = (mapChar(s.charAt(i + 1)) * 8);
            int b3 = mapChar(s.charAt(i + 2));
            int index = (i + 1) / 3;
            int b = (b1 + b2 + b3);
            if (b > 255) throw new SmartFrogParseException("Byte data too large:  " + b +
                    " at byte " + (i/3) +
                    ". Data at line: "+ line +
                    " column:"+ column);
            array[index] = (byte) b;  // note caste down may become negative, overflow impossible
        }
    }

    /**
     * Process the Bin string
     *
     * @param s String
     */
    private void processBINString(String s, int line, int column) throws SmartFrogParseException {
        array = new byte[s.length() / 8];
        for (int i = 0; i < s.length(); i = i + 8) {
            int b1 = (mapChar(s.charAt(i)) << 7);
            int b2 = (mapChar(s.charAt(i + 1)) << 6);
            int b3 = (mapChar(s.charAt(i + 2)) << 5);
            int b4 = (mapChar(s.charAt(i + 3)) << 4);
            int b5 = (mapChar(s.charAt(i + 4)) << 3);
            int b6 = (mapChar(s.charAt(i + 5)) << 2);
            int b7 = (mapChar(s.charAt(i + 6)) << 1);
            int b8 = (mapChar(s.charAt(i + 7)));
            int index = (i + 1)/8;
            int b = (b1 + b2 + b3 + b4 + b5 + b6 +  b7 + b8);
            if (b > 255) throw new SmartFrogParseException("Byte data too large:  " + b +
                    " at byte " + (i/8) +
                    ". Data at line: "+ line +
                    " column:"+ column);
            array[index] = (byte) b;  // note caste down may become negative, overflow impossible
        }
    }


    private static char[] indexstring = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F'};

    /**
     * Process the array
     * @return String
     */
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
