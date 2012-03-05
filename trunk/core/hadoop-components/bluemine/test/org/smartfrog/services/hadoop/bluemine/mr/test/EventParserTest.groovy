package org.smartfrog.services.hadoop.bluemine.mr.test

import org.junit.Test
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventParser
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

/**
 *
 */
class EventParserTest extends BluemineTestBase {

    String[] lines = [
            "gate1,02e73779c77fcd4e9f90a193c4f3e7ff,,2006-10-30,0:06:43,",
            "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:06:54,Smiley",
            "gate3,f1191b79236083ce59981e049d863604,,2006-1-1,23:06:57,vklaptop"
    ]

    @Test
    public void testParser() throws Throwable {
        EventParser parser = new EventParser()

        lines.each { line ->
            LOG.info("Parsing : $line")
            BlueEvent event = parser.parse(line)
            LOG.info("Parsed: $event")
        }
    }

    @Test
    public void testTrim() throws Throwable {
        EventParser parser = new EventParser()
        assertEquals("trimmed", parser.trim(" trimmed "));
        assertEquals("", parser.trim("  "));
        assertEquals(null, parser.trim(null));
    }

    @Test
    public void testDateOff() throws Throwable {
        EventParser parser = new EventParser()
        parser.parseDatestamp = false
        lines.each { line ->
            LOG.info("Parsing : $line")
            BlueEvent event = parser.parse(line)
            assertNull("Expected no date in " + event.toString(), event.datestamp);
            LOG.info("Parsed: $event")
        }

    }

}
