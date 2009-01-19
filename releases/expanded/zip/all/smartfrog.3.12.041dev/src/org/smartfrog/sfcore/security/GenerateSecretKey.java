package org.smartfrog.sfcore.security;

import javax.crypto.SecretKey;
import javax.crypto.KeyGenerator;
import java.io.*;
import java.security.SecureRandom;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;

/**
 * This is only necesary until we commit to jdk 1.6 at which point keeytool introduces
 * the genSercretKey command.
 */
public class GenerateSecretKey {
   private static final String keystoreOpt = "-file";
   private static final String passwordOpt = "-password";
   private static final String aliasOpt = "-alias";


   private static final String usage = "usage: java org.smartfrog.sfcore.security.GenSecretKey" +
         " " + keystoreOpt + " <filename>" +
         " " + passwordOpt + " <password>" +
         " " + aliasOpt + " <alias>";

   public static void main(String[] args) throws IOException, KeyStoreException, NoSuchAlgorithmException, CertificateException {
      String keystore = "";
      String password = "";
      String alias = "";
      SecureRandom rand = new SecureRandom();
      byte[] key = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0};

      if (args.length != 6) {
         System.out.println(usage);
         System.exit(1);
      }

      for (int i = 0; i < args.length; i += 2) {
         if (((String) args[i]).equals(keystoreOpt)) {
            keystore = args[i + 1];
         } else if (((String) args[i]).equals(passwordOpt)) {
            password = args[i + 1];
         } else if (((String) args[i]).equals(aliasOpt)) {
            alias = args[i + 1];
         }else {
            System.out.println(usage);
            System.exit(1);
         }
      }

      if (keystore.equals("") || password.equals("") || alias.equals("")) {
         System.out.println(usage);
         System.exit(1);
      }

      System.out.println("Generating secret key, alias " + alias + ", keystore " + keystore);

      InputStream is = new FileInputStream(keystore);
      KeyStore ks = KeyStore.getInstance("JCEKS");
      ks.load(is, password.toCharArray());
      is.close();

      SecretKey mySecretKey = KeyGenerator.getInstance("HmacSHA1").generateKey();
      KeyStore.SecretKeyEntry skEntry =
          new KeyStore.SecretKeyEntry(mySecretKey);
      ks.setEntry(alias, skEntry, new KeyStore.PasswordProtection(password.toCharArray()));

      OutputStream os = new FileOutputStream(keystore);
      ks.store(os, password.toCharArray());
      os.close();

      System.out.println("Successfully generated secret key, alias " + alias + ", keystore " + keystore);
   }
}
