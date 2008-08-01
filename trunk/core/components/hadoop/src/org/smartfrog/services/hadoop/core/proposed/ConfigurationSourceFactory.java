/*
 * Copyright  2008 The Apache Software Foundation
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */
package org.smartfrog.services.hadoop.core.proposed;

import java.io.IOException;

/**
 * Created 15-May-2008 13:17:15
 */

public interface ConfigurationSourceFactory {

    /**
     * Create a new configuration source
     *
     * @return the source
     * @throws IOException for any failure to create/bind
     */
    public ConfigurationSource createConfigurationSource() throws IOException;


    /**
     * Close the source. This is only guaranteed to be done when the factory singleton is replaced by a new one; when
     * the JVM terminates there will be no notification.
     */
    public void close();
}
