package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.NodeEvent;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;

/**
 * @see imported from PyYAML
 */
public class PyEmitterTest extends PyImportTest {

    private void process(String mask, boolean canonical) throws IOException {
        File[] files = getStreamsByExtension(mask, true);
        assertTrue("No test files found.", files.length > 0);
        for (File file : files) {
            // if (!file.getName().contains("spec-06-01.canonical")) {
            // continue;
            // }
            try {
                List<Event> events = new LinkedList<Event>();
                Reader reader = new Reader(new FileInputStream(file));
                Scanner scanner = new ScannerImpl(reader);
                Parser parser = new ParserImpl(scanner);
                while (parser.peekEvent() != null) {
                    Event event = parser.getEvent();
                    events.add(event);
                }
                //
                StringWriter stream = new StringWriter();
                DumperOptions options = new DumperOptions();
                options.canonical(canonical);
                Emitter emitter = new Emitter(stream, options);
                for (Event event : events) {
                    emitter.emit(event);
                }
                //
                String data = stream.toString();
                List<Event> newEvents = new LinkedList<Event>();
                reader = new Reader(data);
                scanner = new ScannerImpl(reader);
                parser = new ParserImpl(scanner);
                while (parser.peekEvent() != null) {
                    Event event = parser.getEvent();
                    newEvents.add(event);
                }
                // check
                assertEquals(events.size(), newEvents.size());
                Iterator<Event> iter1 = events.iterator();
                Iterator<Event> iter2 = newEvents.iterator();
                while (iter1.hasNext()) {
                    Event event = iter1.next();
                    Event newEvent = iter2.next();
                    assertEquals(event.getClass().getName(), newEvent.getClass().getName());
                    if (event instanceof NodeEvent) {
                        NodeEvent e1 = (NodeEvent) event;
                        NodeEvent e2 = (NodeEvent) newEvent;
                        assertEquals(e1.getAnchor(), e2.getAnchor());
                    }
                    if (event instanceof CollectionStartEvent) {
                        CollectionStartEvent e1 = (CollectionStartEvent) event;
                        CollectionStartEvent e2 = (CollectionStartEvent) newEvent;
                        assertEquals(e1.getTag(), e2.getTag());
                    }
                    if (event instanceof ScalarEvent) {
                        ScalarEvent e1 = (ScalarEvent) event;
                        ScalarEvent e2 = (ScalarEvent) newEvent;
                        boolean[] implicit1 = e1.getImplicit();
                        boolean[] implicit2 = e2.getImplicit();
                        if (!implicit1[0] && !implicit2[0]) {
                            assertEquals(e1.getTag(), e2.getTag());
                        }
                        assertEquals(e1.getValue(), e2.getValue());
                    }
                }
            } catch (Exception e) {
                System.out.println("Failed File: " + file);
                // fail("Failed File: " + file + "; " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void testEmitterOnData() throws IOException {
        process(".data", false);
    }

    public void testEmitterOnCanonicalNormally() throws IOException {
        process(".canonical", false);
    }

    public void testEmitterOnCanonicalCanonically() throws IOException {
        process(".canonical", true);
    }
}
