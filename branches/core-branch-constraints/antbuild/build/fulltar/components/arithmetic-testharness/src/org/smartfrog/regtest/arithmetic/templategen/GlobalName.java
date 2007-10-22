package org.smartfrog.regtest.arithmetic.templategen;


/** Auxiliary class to generate unique names.
 *
 */
public class GlobalName {
    
  static int counter =0;
  static int counter2 =0;

  public static String getName(String in) {
    return in + Integer.toString(counter++);
  }

  public static String getName(String in, int modulus) {
    return in + Integer.toString((counter2++)%modulus);
  }

}  
