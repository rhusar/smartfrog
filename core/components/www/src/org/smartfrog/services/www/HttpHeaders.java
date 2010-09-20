/* (C) Copyright 2010 Hewlett-Packard Development Company, LP

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

package org.smartfrog.services.www;

/**
 * Any useful Http Headers
 */


public interface HttpHeaders {

    /**
     * {@value}
     */

    String CONNECTION = "Connection";

    /**
    /**
     * {@value}
     */

    String CONTENT_LENGTH = "Content-Length";
    /**
     * {@value}
     */

    String CONTENT_TYPE = "Content-Type";
    /**
     * {@value}
     */
    String EXPIRES = "Expires";

    /**
     * {@value}
     */
    String CACHE_CONTROL = "Cache-Control";

    /**
     * {@value}
     */
    String NO_STORE_OR_CACHE = "no-store, no-cache, must-revalidate";
    /**
     * {@value}
     */
    String NO_CACHE = "no-cache";
    /**
     * {@value}
     */
    String FORMAT_BINARY = "application/binary";
    /**
     * {@value}
     */
    String TEXT_HTML = "text/html";
    /**
     * {@value}
     */
    String TEXT_PLAIN = "text/plain";
}
