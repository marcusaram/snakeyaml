package org.yaml.snakeyaml.composer;

import junit.framework.TestCase;

import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class ComposerImplTest extends TestCase {

    public void testGetNode() {
        String data = "american:\n  - Boston Red Sox";
        Node node = compose(data);
        assertNotNull(node);
        assertTrue(node instanceof MappingNode);
        // System.out.println(node);
        String data2 = "---\namerican:\n- Boston Red Sox";
        Node node2 = compose(data2);
        assertNotNull(node2);
        assertEquals(node, node2);
    }

    private Node compose(String data) {
        Reader reader = new Reader(data);
        Scanner scanner = new ScannerImpl(reader);
        Parser parser = new ParserImpl(scanner);
        Resolver resolver = new Resolver();
        Composer composer = new ComposerImpl(parser, resolver);
        Node node = composer.getSingleNode();
        return node;
    }

}
