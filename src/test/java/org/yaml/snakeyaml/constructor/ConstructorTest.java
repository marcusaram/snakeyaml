package org.yaml.snakeyaml.constructor;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.composer.ComposerImpl;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.resolver.ResolverImpl;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class ConstructorTest extends TestCase {

    public void testGetSingleData() {
        String data = "american:\n  - Boston Red Sox";
        Object map = construct(data);
        assertNotNull(map);
        assertTrue(map.getClass().toString(), map instanceof Map);
        assertTrue(map.getClass().toString(), map instanceof HashMap);
        // TODO assertTrue(map.getClass().toString(), map instanceof
        // LinkedHashMap);
    }

    private Object construct(String data) {
        Reader reader = new Reader(data);
        Scanner scanner = new ScannerImpl(reader);
        Parser parser = new ParserImpl(scanner);
        Resolver resolver = new ResolverImpl();
        Composer composer = new ComposerImpl(parser, resolver);
        Constructor constructor = new ConstructorImpl(composer);
        Object result = constructor.getSingleData();
        return result;
    }
}
