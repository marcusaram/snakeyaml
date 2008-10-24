/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

/**
 * @see http://yaml.org/type/int.html
 */
public class IntTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(String data) {
        Map<String, Object> nativeData = (Map<String, Object>) YAML.load(data);
        return nativeData;
    }

    public void testInt() throws IOException {
        assertEquals(new Long(685230), getData("canonical: 685230").get("canonical"));
        assertEquals(new Long(685230), getData("number: 685_230").get("number"));
        assertEquals(new Long(685230), getData("decimal: +685230").get("decimal"));
        assertEquals(new Long(-685230), getData("number: -685230").get("number"));
        assertEquals(new Long(685230), getData("octal: 02472256").get("octal"));
        assertEquals(new Long(685230), getData("hexadecimal: 0x_0A_74_AE").get("hexadecimal"));
        assertEquals(new Long(685230), getData("binary: 0b1010_0111_0100_1010_1110").get("binary"));
        // TODO it must be also Long
        assertEquals(new Integer(685230), getData("sexagesimal: 190:20:30").get("sexagesimal"));
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
