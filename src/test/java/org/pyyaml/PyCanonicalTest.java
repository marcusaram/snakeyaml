package org.pyyaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedList;
import java.util.List;

import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.tokens.Token;

/**
 * @see imported from PyYAML
 */
public class PyCanonicalTest extends PyImportTest {

    public void testCanonicalScanner() throws IOException {
        File[] files = getStreamsByExtension(".canonical");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            System.out.println("Try: " + files[i]);
            List<Token> tokens = canonicalScan(new FileInputStream(files[i]));
            assertFalse(tokens.isEmpty());
        }
    }

    private List<Token> canonicalScan(InputStream input) throws IOException {
        int ch = input.read();
        StringBuffer buffer = new StringBuffer();
        while (ch != -1) {
            buffer.append((char) ch);
            ch = input.read();
        }
        CanonicalScanner scanner = new CanonicalScanner(buffer.toString());
        List<Token> result = new LinkedList<Token>();
        while (scanner.peekToken() != null) {
            // System.out.println("added: " + scanner.peekToken());
            result.add(scanner.getToken());
        }
        return result;
    }

    public void testCanonicalParser() throws IOException {
        File[] files = getStreamsByExtension(".canonical");
        assertTrue("No test files found.", files.length > 0);
        for (int i = 0; i < files.length; i++) {
            System.out.println("Try111: " + files[i]);
            List<Event> tokens = canonicalParse(new FileInputStream(files[i]));

            assertFalse(tokens.isEmpty());
            break;
        }
    }

    private List<Event> canonicalParse(InputStream input) throws IOException {
        int ch = input.read();
        StringBuffer buffer = new StringBuffer();
        while (ch != -1) {
            buffer.append((char) ch);
            ch = input.read();
        }
        CanonicalParser parser = new CanonicalParser(buffer.toString());
        List<Event> result = new LinkedList<Event>();
        while (parser.peekEvent() != null) {
            System.out.println("added: " + parser.peekEvent());
            result.add(parser.getEvent());
        }
        return result;
    }
}
