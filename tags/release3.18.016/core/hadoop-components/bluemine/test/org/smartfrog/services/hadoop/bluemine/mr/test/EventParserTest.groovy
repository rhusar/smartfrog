package org.smartfrog.services.hadoop.bluemine.mr.test

import org.junit.Test
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventParser
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

/**
 *
 */
class EventParserTest extends BluemineTestBase {

    private final String comma1 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2007-09-06,21:34:11,,) Where am i?"
    private final String comma2 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2007-09-06,21:34:11,)\"\', Where am i?"
    private final String[] lines = [
            "gate1,02e73779c77fcd4e9f90a193c4f3e7ff,,2006-10-30,0:06:43,",
            "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:06:54,Smiley",
            "gate3,f1191b79236083ce59981e049d863604,,2006-1-1,23:06:57,vklaptop",
            comma1
    ]

    /**
     * These are erroneous records that show up in the real dataset
     */
    private final String[] badlines = [
            ",45c015c602e28f3f790e2937ff7a8a0b,,2009-01-21,09:14,",
            ",,"
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

    @Test
    public void testTroublesomeName() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(comma1)
        assertNotNull("Null name from $comma1 -> $event", event.name)
        assertEquals(",) Where am i?", event.name)
    }

    @Test
    public void testTroublesomeName2() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(comma2)
        assertNotNull("Null name from $comma2 -> $event", event.name)
        assertEquals(")\"\', Where am i?", event.name)
    }
}
