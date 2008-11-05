package org.jvyaml;

import java.io.FileInputStream;
import java.io.FileReader;
import java.io.Reader;
import java.util.Iterator;

import junit.framework.TestCase;

public class ParserImplTest extends TestCase {

    public void testParserImpl() throws Exception {
        main(new String[0]);
    }

    public static void tmain(final String[] args) throws Exception {
        String filename;
        if (args.length == 1) {
            filename = args[0];
        } else {
            filename = "src/test/resources/specification/example2_28.yaml";
        }
        System.out.println("Reading of file: \"" + filename + "\"");

        final StringBuffer input = new StringBuffer();
        final Reader reader = new FileReader(filename);
        char[] buff = new char[1024];
        int read = 0;
        while (true) {
            read = reader.read(buff);
            input.append(buff, 0, read);
            if (read < 1024) {
                break;
            }
        }
        reader.close();
        final String str = input.toString();
        final long before = System.currentTimeMillis();
        for (int i = 0; i < 1; i++) {
            final Parser pars = new ParserImpl(new ScannerImpl(
                    new org.yaml.snakeyaml.reader.Reader(str)), new DefaultYAMLConfig());
            for (final Iterator iter = pars.eachEvent(); iter.hasNext(); iter.next()) {
            }
        }
        final long after = System.currentTimeMillis();
        final long time = after - before;
        final double timeS = (after - before) / 1000.0;
        System.out.println("Walking through the events for the file: " + filename + " took " + time
                + "ms, or " + timeS + " seconds");
    }

    public static void main(final String[] args) throws Exception {
        String filename;
        if (args.length == 1) {
            filename = args[0];
        } else {
            filename = "src/test/resources/specification/example2_28.yaml";
        }
        final Parser pars = new ParserImpl(new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(
                new FileInputStream(filename))), new DefaultYAMLConfig());
        for (final Iterator iter = pars.eachEvent(); iter.hasNext();) {
            System.out.println(iter.next().getClass().getName());
        }
    }
}
