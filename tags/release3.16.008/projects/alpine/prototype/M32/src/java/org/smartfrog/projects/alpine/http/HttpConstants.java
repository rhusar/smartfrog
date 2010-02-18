/** (C) Copyright 2006 Hewlett-Packard Development Company, LP

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

/**
 *  This file was originally in Apache Axis 1.x.
 *  It has been modified since then
 * -moved to a new location
 * -some extra headers.
 */

/*
 * Copyright 2001-2004 The Apache Software Foundation.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.smartfrog.projects.alpine.http;

/**
 * HTTP protocol and message context constants.
 *
 * @author Rob Jellinghaus
 * @author Doug Davis 
 * @author Jacek Kopecky
 */
public final class HttpConstants {

    private HttpConstants() {
    }

    public static final String CONTENT_TYPE_TEXT_XML = "text/xml";


    public static final String CONTENT_TYPE_SOAP_XML = "application/soap+xml";
    public static final String ALPINE_VERSION = "Alpine $Id$";

    public static final String HEADER_PROTOCOL_10 = "HTTP/1.0";
    public static final String HEADER_PROTOCOL_11 = "HTTP/1.1";
    public static final String HEADER_PROTOCOL_V10 = "1.0".intern();
    public static final String HEADER_PROTOCOL_V11 = "1.1".intern();
    public static final String HEADER_POST = "POST";
    public static final String HEADER_HOST = "Host";
    public static final String HEADER_CONTENT_DESCRIPTION = "Content-Description";
    public static final String HEADER_CONTENT_TYPE = "Content-Type";
    public static final String HEADER_CONTENT_TRANSFER_ENCODING = "Content-Transfer-Encoding";
    public static final String HEADER_CONTENT_TYPE_JMS = "ContentType";
    public static final String HEADER_CONTENT_LENGTH = "Content-Length";
    public static final String HEADER_CONTENT_LOCATION = "Content-Location";
    public static final String HEADER_CONTENT_ID = "Content-Id";
    public static final String HEADER_SOAP_ACTION = "SOAPAction";
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_PROXY_AUTHORIZATION = "Proxy-Authorization";
    public static final String HEADER_EXPECT = "Expect";
    public static final String HEADER_EXPECT_100_Continue = "100-continue";
    public static final String HEADER_USER_AGENT = "User-Agent";
    public static final String HEADER_CACHE_CONTROL = "Cache-Control";
    public static final String HEADER_CACHE_CONTROL_NOCACHE = "no-cache";
    public static final String HEADER_PRAGMA = "Pragma";
    public static final String HEADER_LOCATION = "Location";


    public static final String REQUEST_HEADERS = "HTTP-Request-Headers";
    public static final String RESPONSE_HEADERS = "HTTP-Response-Headers";

    /*http 1.1*/
    public static final String HEADER_TRANSFER_ENCODING = "Transfer-Encoding".intern();
    public static final String HEADER_TRANSFER_ENCODING_CHUNKED = "chunked".intern();

    public static final String HEADER_CONNECTION = "Connection";
    public static final String HEADER_CONNECTION_CLOSE = "close".intern();
    public static final String HEADER_CONNECTION_KEEPALIVE = "Keep-Alive".intern();//The default don't send.

    public static final String HEADER_ACCEPT = "Accept";
    public static final String HEADER_ACCEPT_TEXT_ALL = "text/*";
    public static final String HEADER_ACCEPT_APPL_SOAP = "application/soap+xml";
    public static final String HEADER_ACCEPT_MULTIPART_RELATED = "multipart/related";
    public static final String HEADER_ACCEPT_APPLICATION_DIME = "application/dime";


    /**
     * Cookie headers
     */
    public static final String HEADER_COOKIE = "Cookie";
    public static final String HEADER_COOKIE2 = "Cookie2";
    public static final String HEADER_SET_COOKIE = "Set-Cookie";
    public static final String HEADER_SET_COOKIE2 = "Set-Cookie2";

    /**


    /**
     * HTTP header field values
     */
    public static final String HEADER_DEFAULT_CHAR_ENCODING = "iso-8859-1";


}
