/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.List;

/**
 * @see http://yaml.org/type/null.html
 */
public class NullTagTest extends AbstractTest {

    public void testNull() throws IOException {
        assertNull(load("---\n"));
        assertNull(load("---\n..."));
        assertNull(load("---\n...\n"));
        assertNull(load("\n"));
        assertNull(load(""));
        assertNull(load(" "));
        assertNull(load("~"));
        assertNull(load("---\n~"));
        assertNull(load("null"));
        assertNull(load("Null"));
        assertNull(load("NULL"));
        assertNull(getMapValue("empty:\n", "empty"));
        assertNull(getMapValue("canonical: ~", "canonical"));
        assertNull(getMapValue("english: null", "english"));
        assertNull(getMapValue("english: Null", "english"));
        assertNull(getMapValue("english: NULL", "english"));
        assertEquals("null key", getMapValue("~: null key\n", null));
    }

    @SuppressWarnings("unchecked")
    public void testSequenceNull() throws IOException {
        String input = "---\n# This sequence has five\n# entries, two have values.\nsparse:\n  - ~\n  - 2nd entry\n  -\n  - 4th entry\n  - Null\n";
        List<String> parsed = (List<String>) getMapValue(input, "sparse");
        assertEquals(5, parsed.size());
        assertNull(parsed.get(0));
        assertEquals("2nd entry", parsed.get(1));
        assertNull(parsed.get(2));
        assertEquals("4th entry", parsed.get(3));
        assertNull(parsed.get(4));
    }

    public void testBoolShorthand() throws IOException {
        assertNull(getMapValue("nothing: !!null null", "nothing"));
    }

    public void testBoolTag() throws IOException {
        assertNull(getMapValue("nothing: !<tag:yaml.org,2002:null> null", "nothing"));
    }

    public void testBoolOut() throws IOException {
        String output = dump(null);
        // TODO must contain no additional space and no !!
        assertEquals("--- !!null\n", output);
    }

}
