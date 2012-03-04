package org.smartfrog.services.hadoop.grumpy.test

import org.smartfrog.services.hadoop.grumpy.GrumpyHadoopTestBase
import org.junit.Test
import org.smartfrog.services.hadoop.grumpy.GrumpyTools

/**
* Licensed to the Apache Software Foundation (ASF) under one
* or more contributor license agreements.  See the NOTICE file
* distributed with this work for additional information
* regarding copyright ownership.  The ASF licenses this file
* to you under the Apache License, Version 2.0 (the
* "License"); you may not use this file except in compliance
* with the License.  You may obtain a copy of the License at
*
*     http://www.apache.org/licenses/LICENSE-2.0
*
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
*/
class ToolsTest extends GrumpyHadoopTestBase {

    @Test
    public void testJoinEmpty() throws Throwable {
        assertEquals("", GrumpyTools.joinList([],","));
    }

    @Test
    public void testJoinOne() throws Throwable {
        assertEquals("one", GrumpyTools.joinList(["one"],","));
    }

    @Test
    public void testJoinMore() throws Throwable {
        assertEquals("one,two,three,four", 
                GrumpyTools.joinList(["one","two", "three", "four"],","));
    }
}
