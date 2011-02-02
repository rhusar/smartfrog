// ========================================================================
// Copyright 2004-2005 Mort Bay Consulting Pty. Ltd.
// ------------------------------------------------------------------------
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
// http://www.apache.org/licenses/LICENSE-2.0
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
// ========================================================================

package org.smartfrog.services.jetty.log;

/**
 * This class is apache licensed, code is from Jetty's {@link org.mortbay.log.StdErrLog} class
 */
public class Printf {
    /**
     * Print something with up to two arguments inserted
     *
     * @param message text message
     * @param arg0 first (optional) argument
     * @param arg1 second (optional) argument
     * @return a formatted string
     */
    static String printf(String message, Object arg0, Object arg1) {
        int i0 = message.indexOf("{}");
        int i1 = i0 < 0 ? -1 : message.indexOf("{}", i0 + 2);
        String result = message;


        if (arg1 != null && i1 >= 0) {
            result = result.substring(0, i1) + arg1 + result.substring(i1 + 2);
        }
        if (arg0 != null && i0 >= 0) {
            result = result.substring(0, i0) + arg0 + result.substring(i0 + 2);
        }
        return result;
    }
}
