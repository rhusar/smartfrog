package org.smartfrog.services.hadoop.bluemine.mr.test

import org.junit.Test
import org.smartfrog.services.hadoop.bluemine.events.BlueEvent
import org.smartfrog.services.hadoop.bluemine.events.EventParser
import org.smartfrog.services.hadoop.bluemine.mr.testtools.BluemineTestBase

/**
 *
 */
class EventParserTest extends BluemineTestBase {

    public static final String NO_NAME = "gate1,02e73779c77fcd4e9f90a193c4f3e7ff,,2006-10-30,0:06:43,"
    private static final String SMILEY = "gate1,2afaf990ce75f0a7208f7f012c8d12ad,,2006-10-30,16:06:54,Smiley"
    private static final String COMMA1 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2007-09-06,21:34:11,,) Where am i?"
    private static final String COMMA2 = "0017F2A49B6F,5c598739138321f92971dc6f6ec41344,,2007-09-06,21:34:11,)\"\', Where am i?"
    private static final String VKLAPTOP = "gate3,f1191b79236083ce59981e049d863604,,2006-1-1,23:06:57,vklaptop"
    private static final String[] LINES = [
            NO_NAME,
            SMILEY,
            VKLAPTOP,
            COMMA1
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

        LINES.each { line ->
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
        LINES.each { line ->
            LOG.info("Parsing : $line")
            BlueEvent event = parser.parse(line)
            assertNull("Expected no date in " + event.toString(), event.datestamp);
            LOG.info("Parsed: $event")
        }
    }

    @Test
    public void testTroublesomeName() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(COMMA1)
        assertNotNull("Null name from $COMMA1 -> $event", event.name)
        assertEquals(",) Where am i?", event.name)
    }

    @Test
    public void testTroublesomeName2() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(COMMA2)
        assertNotNull("Null name from $COMMA2 -> $event", event.name)
        assertEquals(")\"\', Where am i?", event.name)
    }

    public void testParsedEventCloneable() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(NO_NAME)
        event.clone()
    }

    public void testParseRoundTripNoName() throws Throwable {
        assertRoundTrip(NO_NAME)
    }

    def assertRoundTrip(String original) {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(original)
        String csv = parser.convertToCSV(event);
        log.info("${original} => {$csv}")
        BlueEvent ev2 = parser.parse(csv)
        assertEventsEqual(event, ev2)
    }

    def assertEventsEqual(BlueEvent expected, BlueEvent actual) {
        assert actual.gate == expected.gate
        assert actual.device == expected.device
        assert actual.datestamp == expected.datestamp
        assert actual.name == expected.name
        assert actual.duration == expected.duration
    }

    public void testParseRoundTripComma1() throws Throwable {
        assertRoundTrip(COMMA1);
    }

    public void testParseRoundTripComma2() throws Throwable {
        assertRoundTrip(COMMA2);
    }

    public void testParseRoundTripDuration() throws Throwable {
        EventParser parser = new EventParser()
        BlueEvent event = parser.parse(VKLAPTOP)
        event.duration = 4096
        String csv = parser.convertToCSV(event)
        log.info("${VKLAPTOP} => {$csv}")
        BlueEvent ev2 = parser.parse(csv)
        assertEventsEqual(event, ev2);
        log.info("Reparsed = $ev2")
    }

}
