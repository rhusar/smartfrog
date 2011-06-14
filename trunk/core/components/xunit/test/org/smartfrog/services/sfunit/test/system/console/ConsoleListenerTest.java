package org.smartfrog.services.sfunit.test.system.console;

import org.junit.Test;
import org.smartfrog.services.xunit.listeners.ConsoleListenerImpl;

/**
 *
 *
 *
 */

public class ConsoleListenerTest {

    @Test
    public void testConsoleListener() throws Throwable {
        ConsoleListenerImpl cl= new ConsoleListenerImpl();
        cl.println("In testConsoleListener");
    }
    
}
