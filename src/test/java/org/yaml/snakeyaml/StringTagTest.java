/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

public class StringTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(String data) {
        Map<String, Object> nativeData = (Map<String, Object>) YAML.load(data);
        return nativeData;
    }

    public void testString() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("string: abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringShorthand() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("string: !!str abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringTag() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("string: !<tag:yaml.org,2002:str> abcd");
        assertEquals("abcd", nativeData.get("string"));
    }

    public void testStringOut() throws IOException {
        Map<String, String> map = new HashMap<String, String>();
        map.put("number", "1");
        String output = YAML.dump(map);
        assertTrue(output.contains("number: !!str 1"));
    }
}
