package org.smartfrog.services.hadoop.bluemine.mr.test;

import groovy.util.GroovyTestCase;
import org.apache.hadoop.security.UserGroupInformation;
import org.junit.Test;

public class LoginTest extends GroovyTestCase {

    @Test
    public void testLogin() throws Throwable {
        UserGroupInformation ugi = UserGroupInformation.currentUser;
        assertNotNull(ugi)
    }
}
