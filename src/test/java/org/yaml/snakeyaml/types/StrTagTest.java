/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see http://yaml.org/type/str.html
 */
public class StrTagTest extends AbstractTest {
    @SuppressWarnings("unchecked")
    private String getData(String data, String key) {
        return (String) getMapValue(data, key);
    }

    public void testString() throws IOException {
        assertEquals("abcd", getData("string: abcd", "string"));
    }

    public void testStringShorthand() throws IOException {
        assertEquals("abcd", getData("string: !!str abcd", "string"));
    }

    public void testStringTag() throws IOException {
        assertEquals("abcd", getData("string: !<tag:yaml.org,2002:str> abcd", "string"));
    }

    public void testUnicode() throws IOException {
        // escaped 8-bit unicode character (u-umlaut):
        assertEquals("\u00fc", load("---\n\"\\xfc\""));

        // 2 escaped 8-bit unicode characters (u-umlaut following by e-grave):
        assertEquals("\u00fc\u00e8", load("---\n\"\\xfc\\xe8\""));

        // escaped 16-bit unicode character (em dash):
        assertEquals("\u2014", load("---\n\"\\u2014\""));

        // No test of escaped 32-bit unicode 'cause I'm not sure what
        // java does with unicode surrogate pairs
        // TODO test 32-bit unicode

        // (and I don't have a surrogate pair handy at the moment)
        // raw unicode characters in the stream (em dash)
        assertEquals("\u2014", load("---\n\u2014"));
    }

    public void testStringIntOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1");
        String output = dump(map);
        assertTrue(output.contains("number: !!str 1"));
    }

    public void testStringFloatOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1.1");
        String output = dump(map);
        assertTrue(output.contains("number: !!str 1.1"));
    }

    public void testStringBoolOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "True");
        String output = dump(map);
        assertTrue(output.contains("number: !!str True"));
    }
}
