package org.jvyaml;

import junit.framework.TestCase;

import org.yaml.snakeyaml.composer.ComposerImpl;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.constructor.ConstructorImpl;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class ConstructorImplTest extends TestCase {

    public void testConstructorImpl() throws Exception {
        main(new String[0]);
    }

    public static void main(final String[] args) throws Exception {
        String filename;
        if (args.length == 1) {
            filename = args[0];
        } else {
            filename = "src/test/resources/specification/example2_28.yaml";
        }
        System.out.println("Reading of file: \"" + filename + "\"");

        final StringBuffer input = new StringBuffer();
        final java.io.Reader reader = new java.io.FileReader(filename);
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
            final Constructor ctor = new ConstructorImpl(new ComposerImpl(new ParserImpl(
                    new ScannerImpl(new org.yaml.snakeyaml.reader.Reader(str))), new Resolver()));
            while (ctor.checkData()) {
                System.out.println(ctor.getData());
            }
        }
        final long after = System.currentTimeMillis();
        final long time = after - before;
        final double timeS = (after - before) / 1000.0;
        System.out.println("Walking through the nodes for the file: " + filename + " took " + time
                + "ms, or " + timeS + " seconds");
    }
}
