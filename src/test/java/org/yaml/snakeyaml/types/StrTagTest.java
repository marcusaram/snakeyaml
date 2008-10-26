/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jvyaml.YAML;

/**
 * @see http://yaml.org/type/str.html
 */
public class StrTagTest extends AbstractTest {
    @SuppressWarnings("unchecked")
    private String getData(String data, String key) {
        Map nativeData = getMap(data);
        return (String) nativeData.get(key);
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

    public void testStringIntOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str 1"));
    }

    public void testStringFloatOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1.1");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str 1.1"));
    }

    public void testStringBoolOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "True");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str True"));
    }
}
