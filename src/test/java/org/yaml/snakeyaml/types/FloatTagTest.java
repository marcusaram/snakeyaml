/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @see http://yaml.org/type/float.html
 */
public class FloatTagTest extends AbstractTest {

    public void testFloat() throws IOException {
        assertEquals(new Double(6.8523015e+5), getMapValue("canonical: 6.8523015e+5", "canonical"));
        assertEquals(new Double(6.8523015e+5), getMapValue("exponentioal: 685.230_15e+03",
                "exponentioal"));
        assertEquals(new Double(6.8523015e+5), getMapValue("fixed: 685_230.15", "fixed"));
        assertEquals(new Double(6.8523015e+5), getMapValue("sexagesimal: 190:20:30.15",
                "sexagesimal"));
        assertEquals(Double.NEGATIVE_INFINITY, getMapValue("negative infinity: -.inf",
                "negative infinity"));
        assertEquals(Double.NaN, getMapValue("not a number: .NaN", "not a number"));
    }

    public void testFloatShorthand() throws IOException {
        assertEquals(new Double(1), getMapValue("number: !!float 1", "number"));
    }

    public void testFloatTag() throws IOException {
        assertEquals(new Double(1), getMapValue("number: !<tag:yaml.org,2002:float> 1", "number"));
    }

    public void testFloatOut() throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("number", new Double(1));
        String output = dump(map);
        assertTrue(output.contains("number: 1"));
    }

}
