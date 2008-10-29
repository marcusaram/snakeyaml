/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TimeZone;

import junit.framework.TestCase;

/**
 * Test Chapter 2.4 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Chapter2_4Test extends TestCase {

    @SuppressWarnings("unchecked")
    public void testExample_2_19() {
        YamlDocument document = new YamlDocument("example2_19.yaml");
        Map<String, Object> map = (Map<String, Object>) document.getNativeData();
        assertEquals(5, map.size());
        assertEquals("Expect 12345 to be an Integer.", Long.class, map.get("canonical").getClass());
        assertEquals(new Long(12345), map.get("canonical"));
        assertEquals(new Long(12345), map.get("decimal"));
        assertEquals(new Long(3 * 3600 + 25 * 60 + 45), map.get("sexagesimal"));
        assertEquals(new Long(014), map.get("octal"));
        assertEquals(new Long(0xC), map.get("hexadecimal"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_20() {
        YamlDocument document = new YamlDocument("example2_20.yaml");
        Map<String, Object> map = (Map<String, Object>) document.getNativeData();
        assertEquals(6, map.size());
        assertEquals("Expect '1.23015e+3' to be a Double.", Double.class, map.get("canonical")
                .getClass());
        assertEquals(new Double(1230.15), map.get("canonical"));
        assertEquals(new Double(12.3015e+02), map.get("exponential"));
        assertEquals(new Double(20 * 60 + 30.15), map.get("sexagesimal"));
        assertEquals(new Double(1230.15), map.get("fixed"));
        assertEquals(Double.NEGATIVE_INFINITY, map.get("negative infinity"));
        assertEquals(Double.NaN, map.get("not a number"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_21() {
        YamlDocument document = new YamlDocument("example2_21.yaml");
        Map<String, Object> map = (Map<String, Object>) document.getNativeData();
        assertEquals(4, map.size());
        assertNull("'~' must be parsed as 'null': " + map.get("null").toString(), map.get("null"));
        assertEquals("Expect y to be a Boolean: '" + map.get("true") + "'.", Boolean.class, map
                .get("true").getClass());
        assertTrue((Boolean) map.get("true"));
        assertFalse((Boolean) map.get("false"));
        assertEquals("12345", map.get("string"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_22() {
        YamlDocument document = new YamlDocument("example2_22.yaml");
        Map<String, Object> map = (Map<String, Object>) document.getNativeData();
        assertEquals(4, map.size());
        assertEquals("Expect '2001-12-15T02:59:43.1Z' to be a Date.", Date.class, map.get(
                "canonical").getClass());
        Calendar cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(Calendar.YEAR, 2001);
        cal.set(Calendar.MONTH, 11); // Java's months are zero-based...
        cal.set(Calendar.DAY_OF_MONTH, 15);
        cal.set(Calendar.HOUR_OF_DAY, 2);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 43);
        cal.set(Calendar.MILLISECOND, 100);
        Date date = cal.getTime();
        assertEquals(date, map.get("canonical"));
        assertEquals("Expect '2001-12-14t21:59:43.10-05:00' to be a Date.", Date.class, map.get(
                "iso8601").getClass());
        assertEquals("Expect '2001-12-14 21:59:43.10 -5' to be a Date.", Date.class, map.get(
                "spaced").getClass());
        assertEquals("Expect '2002-12-14' to be a Date.", Date.class, map.get("date").getClass());
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_23_non_date() {
        try {
            YamlDocument document = new YamlDocument("example2_23_non_date.yaml");
            Map<String, Object> map = (Map<String, Object>) document.getNativeData();
            assertEquals(1, map.size());
            assertEquals("2002-04-28", map.get("not-date"));
        } catch (RuntimeException e) {
            fail("Cannot parse '!!str': 'not-date: !!str 2002-04-28'");
        }
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_23_picture() throws Exception {
        YamlDocument document = new YamlDocument("example2_23_picture.yaml");
        Map<String, Object> map = (Map<String, Object>) document.getNativeData();
        assertEquals(1, map.size());
        ByteBuffer picture = (ByteBuffer) map.get("picture");
        byte[] gif = picture.array();
        String str = new String(gif);
        assertTrue(str.startsWith("GIF89"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_23_application() throws IOException {
        // TODO unclear how to test "Example 2.23. Various Explicit Tags"
        // fail("Test not finished for: 'Example 2.23. Various Explicit Tags'");
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_24() {
        // TODO unclear how to test "Example 2.24. Global Tags"
        // fail("Test not finished for: 'Example 2.24. Global Tags'");
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_25() {
        try {
            YamlDocument document = new YamlDocument("example2_25.yaml");
            Set<String> set = (Set<String>) document.getNativeData();
            assertEquals(3, set.size());
            assertTrue(set.contains("Mark McGwire"));
            assertTrue(set.contains("Sammy Sosa"));
            assertTrue(set.contains("Ken Griff"));
        } catch (RuntimeException e) {
            // TOTO fail("!!set is not implemented.");
        }
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_26() {
        try {
            YamlDocument document = new YamlDocument("example2_26.yaml");
            Map<String, String> map = (Map<String, String>) document.getNativeData();
            assertEquals(3, map.size());
            assertEquals("65", map.get("Mark McGwire").toString());
            assertEquals("63", map.get("Sammy Sosa").toString());
            assertEquals("58", map.get("Ken Griffy").toString());
            List list = new ArrayList();
            for (String key : map.keySet()) {
                list.add(key);
            }
            assertEquals("Mark McGwire", list.get(0));
            assertEquals("Sammy Sosa", list.get(1));
            assertEquals("Ken Griffy", list.get(2));
        } catch (RuntimeException e) {
            // TODO fail("!!omap is not implemented.");
        }
    }
}
