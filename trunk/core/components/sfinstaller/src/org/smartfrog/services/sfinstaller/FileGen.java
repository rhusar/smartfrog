/* (C) Copyright 2008 Hewlett-Packard Development Company, LP

Disclaimer of Warranty

The Software is provided "AS IS," without a warranty of any kind. ALL
EXPRESS OR IMPLIED CONDITIONS, REPRESENTATIONS AND WARRANTIES,
INCLUDING ANY IMPLIED WARRANTY OF MERCHANTABILITY, FITNESS FOR A
PARTICULAR PURPOSE, OR NON-INFRINGEMENT, ARE HEREBY
EXCLUDED. SmartFrog is not a Hewlett-Packard Product. The Software has
not undergone complete testing and may contain errors and defects. It
may not function properly and is subject to change or withdrawal at
any time. The user must assume the entire risk of using the
Software. No support or maintenance is provided with the Software by
Hewlett-Packard. Do not install the Software if you are not accustomed
to using experimental software.

Limitation of Liability

TO THE EXTENT NOT PROHIBITED BY LAW, IN NO EVENT WILL HEWLETT-PACKARD
OR ITS LICENSORS BE LIABLE FOR ANY LOST REVENUE, PROFIT OR DATA, OR
FOR SPECIAL, INDIRECT, CONSEQUENTIAL, INCIDENTAL OR PUNITIVE DAMAGES,
HOWEVER CAUSED REGARDLESS OF THE THEORY OF LIABILITY, ARISING OUT OF
OR RELATED TO THE FURNISHING, PERFORMANCE, OR USE OF THE SOFTWARE, OR
THE INABILITY TO USE THE SOFTWARE, EVEN IF HEWLETT-PACKARD HAS BEEN
ADVISED OF THE POSSIBILITY OF SUCH DAMAGES. FURTHERMORE, SINCE THE
SOFTWARE IS PROVIDED WITHOUT CHARGE, YOU AGREE THAT THERE HAS BEEN NO
BARGAIN MADE FOR ANY ASSUMPTIONS OF LIABILITY OR DAMAGES BY
HEWLETT-PACKARD FOR ANY REASON WHATSOEVER, RELATING TO THE SOFTWARE OR
ITS MEDIA, AND YOU HEREBY WAIVE ANY CLAIM IN THIS REGARD.

*/
package org.smartfrog.services.sfinstaller;

import org.smartfrog.services.filesystem.FileSystem;

import java.io.PrintStream;
import java.io.FileOutputStream;
import java.io.BufferedWriter;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Collection;
import java.util.Iterator;

/**
 * A class for creating the data file from map The data file is read for creating the sfinstaller description file
 *
 * @author Ritu Sabharwal
 */
public class FileGen {

    /**
     * The seperator for fields of data file
     */
    static char seperator = (char) 255;

    /**
     * The Daemon object to be read from map
     */
    static Daemon host;

    /**
     * An iterator for entries in a map.
     */
    static Iterator t;

    /**
     * Iterator over the map entries and writes to data file
     *
     * @param mapFile map with daemon objects
     */
    static public void createFile(Map mapFile, String inFileName) throws Exception {
        BufferedWriter writer=null;
        try {
            if (!mapFile.isEmpty()) {
                Collection values = mapFile.values();
                t = values.iterator();
            }

            PrintStream out = new PrintStream(new FileOutputStream(inFileName, true));
            writer = new BufferedWriter(new OutputStreamWriter(out));

            while (t.hasNext()) {
                host = (Daemon) t.next();
                writer.write(host.name);
                writer.write(seperator);
                writer.write(host.os);
                writer.write(seperator);
                writer.write(host.host);
                writer.write(seperator);
                writer.write(host.transfertype);
                writer.write(seperator);
                writer.write(host.logintype);
                writer.write(seperator);
                writer.write(host.user);
                writer.write(seperator);
                writer.write(host.password);
                writer.write(seperator);
                writer.write(host.localfile1);
                if (host.os.equals("windows")) {
                    writer.write(seperator);
                    writer.write(host.localfile2);
                    writer.write(seperator);
                    writer.write(host.localfile3);
                }
                if (host.keyfile != null) {
                    writer.write(seperator);
                    writer.write(host.keyfile);
                }
                if (host.secproperties != null) {
                    writer.write(seperator);
                    writer.write(host.secproperties);
                }
                if (host.smartfrogjar != null) {
                    writer.write(seperator);
                    writer.write(host.smartfrogjar);
                }
                if (host.servicesjar != null) {
                    writer.write(seperator);
                    writer.write(host.servicesjar);
                }
                if (host.examplesjar != null) {
                    writer.write(seperator);
                    writer.write(host.examplesjar);
                }
                writer.write(seperator);
                writer.write(host.releasename);
                //  if (host.os.equals("windows")) {
                if (host.javahome != null) {
                    writer.write(seperator);
                    writer.write(host.javahome);
                }
                //  }
                writer.write(seperator);
                writer.write(host.installdir);
                writer.write(seperator);
                writer.write(host.emailto);
                writer.write(seperator);
                writer.write(host.emailfrom);
                writer.write(seperator);
                writer.write(host.emailserver);
                writer.write(seperator);
                writer.write("\n");
            }
            writer.flush();
            writer.close();
            writer=null;
        } finally {
            FileSystem.close(writer);
	  }
	}
}
