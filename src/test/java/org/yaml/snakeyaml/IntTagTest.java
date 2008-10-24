/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

public class IntTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(String data) {
        Map<String, Object> nativeData = (Map<String, Object>) YAML.load(data);
        return nativeData;
    }

    public void testInt() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("number: 1");
        assertEquals(new Long(1), nativeData.get("number"));
    }

    public void testIntShorthand() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("number: !!int 1");
        assertEquals(new Long(1), nativeData.get("number"));
    }

    public void testIntTag() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("number: !<tag:yaml.org,2002:int> 1");
        assertEquals(new Long(1), nativeData.get("number"));
    }

    public void testIntOut() throws IOException {
        Map<String, Long> map = new HashMap<String, Long>();
        map.put("number", new Long(1));
        String output = YAML.dump(map);
        assertTrue(output.contains("number: 1"));
    }

}
