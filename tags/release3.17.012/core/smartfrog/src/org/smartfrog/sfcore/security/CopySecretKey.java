package org.smartfrog.sfcore.security;

import java.io.*;
import java.security.KeyStore;
import java.security.NoSuchAlgorithmException;
import java.security.KeyStoreException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

/**
 * Copy the secret key from one file to another - each encrypted with a different key
 */
public class CopySecretKey {
   private static final String skeystoreOpt = "-sourcefile";
   private static final String spasswordOpt = "-sourcepassword";
   private static final String dkeystoreOpt = "-destfile";
   private static final String dpasswordOpt = "-destpassword";
   private static final String aliasOpt = "-alias";

   private static final String usage = "usage: java org.smartfrog.sfcore.security.CopySecretKey" +
         " " + skeystoreOpt + " <filename>" +
         " " + spasswordOpt + " <password>" +
         " " + dkeystoreOpt + " <filename>" +
         " " + dpasswordOpt + " <password>" +
         " " + aliasOpt + " <alias>";

   public static void main(String[] args) throws IOException, NoSuchAlgorithmException, CertificateException, KeyStoreException, UnrecoverableEntryException {
      String skeystore = "";
      String spassword = "";
      String dkeystore = "";
      String dpassword = "";
      String alias = "";

      if (args.length != 10) {
         System.out.println(usage);
         System.exit(1);
      }

      for (int i = 0; i < args.length; i += 2) {
         if (((String) args[i]).equals(skeystoreOpt)) {
            skeystore = args[i + 1];
         } else if (((String) args[i]).equals(spasswordOpt)) {
            spassword = args[i + 1];
         } else if (((String) args[i]).equals(dkeystoreOpt)) {
            dkeystore = args[i + 1];
         } else if (((String) args[i]).equals(dpasswordOpt)) {
            dpassword = args[i + 1];
         } else if (((String) args[i]).equals(aliasOpt)) {
            alias = args[i + 1];
         } else {
            System.out.println(usage);
            System.exit(1);
         }
      }

      if (skeystore.equals("") || spassword.equals("") || dkeystore.equals("") || dpassword.equals("") || alias.equals(""))  {
         System.out.println(usage);
         System.exit(1);
      }

      System.out.println("Copying secret key, alias " + alias + ", from keystore " + skeystore + ", to keystore " + dkeystore);

      InputStream is = new FileInputStream(skeystore);
      KeyStore ks = KeyStore.getInstance("JCEKS");
      ks.load(is, spassword.toCharArray());
      is.close();

      KeyStore.SecretKeyEntry skEntry = (KeyStore.SecretKeyEntry)ks.getEntry(alias, new KeyStore.PasswordProtection(spassword.toCharArray()));

      is = new FileInputStream(dkeystore);
      ks = KeyStore.getInstance("JCEKS");
      ks.load(is, dpassword.toCharArray());
      is.close();

      ks.setEntry(alias, skEntry, new KeyStore.PasswordProtection(dpassword.toCharArray()));

      OutputStream os = new FileOutputStream(dkeystore);
      ks.store(os, dpassword.toCharArray());
      os.close();

      System.out.println("Successfully copied secret key, alias " + alias + ", from keystore " + skeystore + ", to keystore " + dkeystore);

   }
}
