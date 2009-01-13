/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.events.AliasEvent;
import org.yaml.snakeyaml.events.CollectionStartEvent;
import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.events.ScalarEvent;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.nodes.SequenceNode;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.reader.UnicodeReader;
import org.yaml.snakeyaml.resolver.Resolver;

/**
 * @see imported from PyYAML
 */
public class PyStructureTest extends PyImportTest {

    private void compareEvents(List<Event> events1, List<Event> events2, boolean full) {
        assertEquals(events1.size(), events2.size());
        Iterator<Event> iter1 = events1.iterator();
        Iterator<Event> iter2 = events2.iterator();
        while (iter1.hasNext()) {
            Event event1 = iter1.next();
            Event event2 = iter2.next();
            assertEquals(event1.getClass(), event2.getClass());
            if (event1 instanceof AliasEvent && full) {
                assertEquals(((AliasEvent) event1).getAnchor(), ((AliasEvent) event2).getAnchor());
            }
            if (event1 instanceof CollectionStartEvent) {
                String tag1 = ((CollectionStartEvent) event1).getTag();
                String tag2 = ((CollectionStartEvent) event1).getTag();
                if (tag1 != null && !"!".equals(tag1) && tag2 != null && !"!".equals(tag1)) {
                    assertEquals(tag1, tag2);
                }
            }
            if (event1 instanceof ScalarEvent) {
                ScalarEvent scalar1 = (ScalarEvent) event1;
                ScalarEvent scalar2 = (ScalarEvent) event2;
                boolean[] oldImplicit = scalar1.getImplicit();
                boolean[] newImplicit = scalar2.getImplicit();
                if (!oldImplicit[0] && !oldImplicit[1] && !newImplicit[0] && !newImplicit[1]) {
                    assertEquals(scalar1.getTag(), scalar2.getTag());
                }
                assertEquals(scalar1.getValue(), scalar2.getValue());
            }
        }
    }

    public void testParser() throws IOException {
        File[] canonicalFiles = getStreamsByExtension(".canonical", false);
        assertTrue("No test files found.", canonicalFiles.length > 0);
        File[] files = getStreamsByExtension(".data", true);
        assertTrue("No test files found.", files.length > 0);
        int index = 0;
        for (File file : files) {
            try {
                List<Event> events1 = parse(new FileInputStream(file));
                assertFalse(events1.isEmpty());
                File canonical = canonicalFiles[index++];
                List<Event> events2 = canonicalParse(new FileInputStream(canonical));
                assertFalse(events2.isEmpty());
                compareEvents(events1, events2, false);
            } catch (Exception e) {
                System.out.println("Failed File: " + file);
                // fail("Failed File: " + file + "; " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    public void testParserOnCanonical() throws IOException {
        File[] canonicalFiles = getStreamsByExtension(".canonical", false);
        assertTrue("No test files found.", canonicalFiles.length > 0);
        for (File file : canonicalFiles) {
            try {
                List<Event> events1 = parse(new FileInputStream(file));
                assertFalse(events1.isEmpty());
                List<Event> events2 = canonicalParse(new FileInputStream(file));
                assertFalse(events2.isEmpty());
                compareEvents(events1, events2, true);
            } catch (Exception e) {
                System.out.println("Failed File: " + file);
                // fail("Failed File: " + file + "; " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private void compareNodes(Node node1, Node node2) {
        assertEquals(node1.getClass(), node2.getClass());
        if (node1 instanceof ScalarNode) {
            ScalarNode scalar1 = (ScalarNode) node1;
            ScalarNode scalar2 = (ScalarNode) node2;
            assertEquals(scalar1.getTag(), scalar2.getTag());
            assertEquals(scalar1.getValue(), scalar2.getValue());
        } else {
            if (node1 instanceof SequenceNode) {
                SequenceNode seq1 = (SequenceNode) node1;
                SequenceNode seq2 = (SequenceNode) node2;
                assertEquals(seq1.getTag(), seq2.getTag());
                assertEquals(seq1.getValue().size(), seq2.getValue().size());
                Iterator<Node> iter2 = seq2.getValue().iterator();
                for (Node child1 : seq1.getValue()) {
                    Node child2 = iter2.next();
                    compareNodes(child1, child2);
                }
            } else {
                MappingNode seq1 = (MappingNode) node1;
                MappingNode seq2 = (MappingNode) node2;
                assertEquals(seq1.getTag(), seq2.getTag());
                assertEquals(seq1.getValue().size(), seq2.getValue().size());
                Iterator<Node[]> iter2 = seq2.getValue().iterator();
                for (Node[] child1 : seq1.getValue()) {
                    Node[] child2 = iter2.next();
                    compareNodes(child1[0], child2[0]);// keys
                    compareNodes(child1[1], child2[1]);// values
                }
            }
        }
    }

    public void testComposer() throws IOException {
        File[] canonicalFiles = getStreamsByExtension(".canonical", false);
        assertTrue("No test files found.", canonicalFiles.length > 0);
        File[] files = getStreamsByExtension(".data", true);
        assertTrue("No test files found.", files.length > 0);
        int index = 0;
        for (File file : files) {
            try {
                List<Node> events1 = compose_all(new FileInputStream(file));
                File canonical = canonicalFiles[index++];
                List<Node> events2 = canonical_compose_all(new FileInputStream(canonical));
                assertEquals(events1.size(), events2.size());
                Iterator<Node> iter1 = events1.iterator();
                Iterator<Node> iter2 = events2.iterator();
                while (iter1.hasNext()) {
                    compareNodes(iter1.next(), iter2.next());
                }
            } catch (Exception e) {
                System.out.println("Failed File: " + file);
                // fail("Failed File: " + file + "; " + e.getMessage());
                throw new RuntimeException(e);
            }
        }
    }

    private List<Node> compose_all(InputStream file) {
        Composer composer = new Composer(new ParserImpl(new Reader(new UnicodeReader(file))),
                new Resolver());
        List<Node> documents = new LinkedList<Node>();
        while (composer.checkNode()) {
            documents.add(composer.getNode());
        }
        return documents;
    }

    private List<Node> canonical_compose_all(InputStream file) {
        Reader reader = new Reader(new UnicodeReader(file));
        StringBuffer buffer = new StringBuffer();
        while (reader.peek() != '\0') {
            buffer.append(reader.peek());
            reader.forward();
        }
        CanonicalParser parser = new CanonicalParser(buffer.toString());
        Composer composer = new Composer(parser, new Resolver());
        List<Node> documents = new LinkedList<Node>();
        while (composer.checkNode()) {
            documents.add(composer.getNode());
        }
        return documents;
    }
}
