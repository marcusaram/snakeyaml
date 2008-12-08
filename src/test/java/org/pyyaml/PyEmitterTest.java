package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.events.Event;
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
                Emitter emitter = new Emitter(stream, new DumperOptions());
                for (Event event : events) {
                    emitter.emit(event);
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
