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
package org.smartfrog.services.filesystem.filestore;

import org.smartfrog.services.filesystem.FileUsingComponent;
import org.smartfrog.sfcore.common.SmartFrogException;

import java.net.URI;
import java.rmi.RemoteException;


/** A filestore. */
public interface LocalFilestore extends FileUsingComponent {


    /**
     * Create a new temp file
     *
     * @param prefix prefix
     * @param suffix suffix
     * @return a file entry describing both the file and the URL
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in creating file
     *
     * @throws java.rmi.RemoteException In case of network/rmi error
     */
    FileEntry createNewFile(String prefix, String suffix)
            throws SmartFrogException, RemoteException;

    /**
     * Delete an entry
     *
     * @param uri uri of entry
     * @return true if deletion worked
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in deleting
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */
    boolean delete(URI uri)
            throws SmartFrogException, RemoteException;

    /**
     * look up a file from a URI
     *
     * @param uri  uri
     * @return FileEntry
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in lookup
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */
    FileEntry lookup(URI uri) throws SmartFrogException, RemoteException;

    /**
     * Create a new temp file
     *
     * @param prefix   prefix
     * @param suffix   suffix
     * @param content  the actual content of the file content
     * @param metadata any metadata
     * @return a file entry describing both the file and the URL
     * @throws org.smartfrog.sfcore.common.SmartFrogException error in creating file
     *
     * @throws java.rmi.RemoteException  In case of network/rmi error
     */

    FileEntry uploadNewFile(String prefix,
                            String suffix,
                            byte[] content,
                            Object metadata)
            throws SmartFrogException, RemoteException;

}
