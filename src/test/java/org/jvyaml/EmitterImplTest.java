package org.jvyaml;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;

import junit.framework.TestCase;

import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class EmitterImplTest extends TestCase {

    public void testEmitterImpl() throws IOException {
        main(new String[0]);
    }

    public static void main(final String[] args) throws IOException {
        String filename;
        if (args.length == 1) {
            filename = args[0];
        } else {
            filename = "src/test/resources/specification/example2_28.yaml";
        }
        System.out.println("File contents:");
        final BufferedReader read = new BufferedReader(new FileReader(filename));
        String last = null;
        while ((last = read.readLine()) != null) {
            System.out.println(last);
        }
        read.close();
        System.out.println("--------------------------------");
        final Emitter emitter = new EmitterImpl(new java.io.OutputStreamWriter(System.out),
                new DefaultYAMLConfig());
        final Parser pars = new ParserImpl(new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(
                new FileInputStream(filename))), new DefaultYAMLConfig());
        for (final Iterator iter = pars.eachEvent(); iter.hasNext();) {
            emitter.emit((Event) iter.next());
        }
    }
}
