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
 * @see http://yaml.org/type/float.html
 */
public class FloatTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, Object> getData(String data) {
        Map<String, Object> nativeData = (Map<String, Object>) YAML.load(data);
        return nativeData;
    }

    public void testFloat() throws IOException {
        assertEquals(new Double(6.8523015e+5), getData("canonical: 6.8523015e+5").get("canonical"));
        assertEquals(new Double(6.8523015e+5), getData("exponentioal: 685.230_15e+03").get(
                "exponentioal"));
        assertEquals(new Double(6.8523015e+5), getData("fixed: 685_230.15").get("fixed"));
        assertEquals(new Double(6.8523015e+5), getData("sexagesimal: 190:20:30.15").get(
                "sexagesimal"));
        assertEquals(Double.NEGATIVE_INFINITY, getData("negative infinity: -.inf").get(
                "negative infinity"));
        assertEquals(Double.NaN, getData("not a number: .NaN").get("not a number"));
    }

    public void testFloatShorthand() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("number: !!float 1");
        assertEquals(new Double(1), nativeData.get("number"));
    }

    public void testFloatTag() throws IOException {
        Map<String, Object> nativeData = (Map<String, Object>) getData("number: !<tag:yaml.org,2002:float> 1");
        assertEquals(new Double(1), nativeData.get("number"));
    }

    public void testFloatOut() throws IOException {
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("number", new Double(1));
        String output = YAML.dump(map);
        assertTrue(output.contains("number: 1"));
    }

}
