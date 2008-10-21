package net.sourceforge.yamlbeans.specification;

import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test Chapter 2.5 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Chapter2_5Test extends TestCase {

    @SuppressWarnings("unchecked")
    public void testExample_2_27() {
        // TODO unclear how to test "Example 2.27. Invoice"
        fail("Test not finished for: 'Example 2.27. Invoice'");
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_28() {
        YamlStream resource = new YamlStream("example2_28.yaml");
        List<Object> list = (List<Object>) resource.getNativeData();
        assertEquals(3, list.size());
        Map<String, Object> data0 = (Map<String, Object>) list.get(0);
        assertEquals("2001-11-23 15:01:42 -5", data0.get("Time"));
        assertEquals("ed", data0.get("User"));
        assertEquals("This is an error message for the log file", data0.get("Warning"));
        //
        Map<String, Object> data1 = (Map<String, Object>) list.get(1);
        assertEquals("2001-11-23 15:02:31 -5", data1.get("Time"));
        assertEquals("ed", data1.get("User"));
        assertEquals("A slightly different error message.", data1.get("Warning"));
        //
        Map<String, Object> data3 = (Map<String, Object>) list.get(2);
        assertEquals("2001-11-23 15:03:17 -5", data3.get("Date"));
        assertEquals("ed", data3.get("User"));
        assertEquals("Unknown variable \"bar\"", data3.get("Fatal"));
        List<Map<String, String>> list3 = (List<Map<String, String>>) data3.get("Stack");
        Map<String, String> map1 = list3.get(0);
        assertEquals("TopClass.py", map1.get("file"));
        assertEquals("23", map1.get("line").toString());
        assertEquals("x = MoreObject(\"345\\n\")", map1.get("code"));
        Map<String, String> map2 = list3.get(1);
        assertEquals("MoreClass.py", map2.get("file"));
        assertEquals("58", map2.get("line").toString());
        assertEquals("-\nfoo = bar", map2.get("code"));
    }
}
