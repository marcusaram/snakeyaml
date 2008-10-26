/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.jvyaml.YAML;

/**
 * @see http://yaml.org/type/int.html
 */
public class IntTagTest extends AbstractTest {
    @SuppressWarnings("unchecked")
    private Object getData(String data, String key) {
        Map nativeData = getMap(data);
        return nativeData.get(key);
    }

    public void testInt() throws IOException {
        assertEquals(new Long(685230), getData("canonical: 685230", "canonical"));
        assertEquals(new Long(685230), getData("number: 685_230", "number"));
        assertEquals(new Long(685230), getData("decimal: +685230", "decimal"));
        assertEquals(new Long(-685230), getData("number: -685230", "number"));
        assertEquals(new Long(685230), getData("octal: 02472256", "octal"));
        assertEquals(new Long(685230), getData("hexadecimal: 0x_0A_74_AE", "hexadecimal"));
        assertEquals(new Long(685230), getData("binary: 0b1010_0111_0100_1010_1110", "binary"));
        // TODO it must be also Long
        assertEquals(new Integer(685230), getData("sexagesimal: 190:20:30", "sexagesimal"));
    }

    public void testIntShorthand() throws IOException {
        assertEquals(new Long(1), getData("number: !!int 1", "number"));
    }

    public void testIntTag() throws IOException {
        assertEquals(new Long(1), getData("number: !<tag:yaml.org,2002:int> 1", "number"));
    }

    public void testIntOut() throws IOException {
        Map<String, Long> map = new HashMap<String, Long>();
        map.put("number", new Long(1));
        String output = YAML.dump(map);
        assertTrue(output.contains("number: 1"));
    }

}
