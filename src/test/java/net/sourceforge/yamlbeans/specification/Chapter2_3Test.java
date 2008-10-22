/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package net.sourceforge.yamlbeans.specification;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

/**
 * Test Chapter 2.3 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Chapter2_3Test extends TestCase {

    @SuppressWarnings("unchecked")
    public void testExample_2_13() {
        YamlDocument document = new YamlDocument("example2_13.yaml");
        String data = (String) document.getNativeData();
        assertEquals(21, data.length());
        assertEquals("\\//||\\/||\n// ||  ||__", data);
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_14() {
        YamlDocument document = new YamlDocument("example2_14.yaml");
        String data = (String) document.getNativeData();
        assertEquals("Mark McGwire's year was crippled by a knee injury.", data);
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_15_without_more_indented_lines() {
        String etalon = "Sammy Sosa completed another fine season with great stats.\n\nWhat a year!";
        YamlDocument document = new YamlDocument("example2_15a.yaml");
        String data = (String) document.getNativeData();
        assertEquals(etalon, data);
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_15() {
        String etalon = "Sammy Sosa completed another fine season with great stats.\n  63 Home Runs\n  0.288 Batting Average\nWhat a year!";
        try {
            YamlDocument document = new YamlDocument("example2_15.yaml");
            String data = (String) document.getNativeData();
            assertEquals(etalon, data);
        } catch (RuntimeException e) {
            fail("'More indented' lines are not yet implemented.");
        }
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_16() {
        YamlDocument document = new YamlDocument("example2_16.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals(map.toString(), 3, map.size());
        assertEquals("Mark McGwire", map.get("name"));
        assertEquals("Mark set a major league home run record in 1998.", map.get("accomplishment"));
        assertEquals("65 Home Runs\n0.278 Batting Average", map.get("stats"));

    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17() throws IOException {
        try {
            YamlDocument document = new YamlDocument("example2_17.yaml");
            Map<String, String> map = (Map<String, String>) document.getNativeData();
            assertEquals(map.toString(), 6, map.size());
            assertEquals("Sosa did fine.\u263A", map.get("unicode"));
            assertEquals("\b1998\t1999\t2000\n", map.get("control"));
            assertEquals("\\x13\\x10 is \\r\\n", map.get("hexesc"));
            assertEquals("\"Howdy!\" he cried.", map.get("single"));
            assertEquals(" # not a 'comment'.", map.get("quoted"));
            assertEquals("|\\-*-/|", map.get("tie-fighter"));
        } catch (RuntimeException e) {
            fail("Scalars cannot be parsed: " + YamlDocument.getLocalResource("example2_17.yaml"));
        }
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_unicode() {
        YamlDocument document = new YamlDocument("example2_17_unicode.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals("Sosa did fine.\u263A", map.get("unicode"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_control() {
        YamlDocument document = new YamlDocument("example2_17_control.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals("\b1998\t1999\t2000\n", map.get("control"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_hexesc() {
        YamlDocument document = new YamlDocument("example2_17_hexesc.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals("\\x13\\x10 is \\r\\n", map.get("hexesc"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_single() {
        YamlDocument document = new YamlDocument("example2_17_single.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals("\"Howdy!\" he cried.", map.get("single"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_quoted() {
        try {
            YamlDocument document = new YamlDocument("example2_17_quoted.yaml");
            Map<String, String> map = (Map<String, String>) document.getNativeData();
            assertEquals(" # not a 'comment'.", map.get("quoted"));
        } catch (RuntimeException e) {
            fail("Double ' is not escaped in single quoted scalars.");
        }
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_17_tie_fighter() {
        YamlDocument document = new YamlDocument("example2_17_tie_fighter.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals("|\\-*-/|", map.get("tie-fighter"));
    }

    @SuppressWarnings("unchecked")
    public void testExample_2_18() throws IOException {
        YamlDocument document = new YamlDocument("example2_18.yaml");
        Map<String, String> map = (Map<String, String>) document.getNativeData();
        assertEquals(map.toString(), 2, map.size());
        assertEquals("This unquoted scalar spans many lines.", map.get("plain"));
        assertEquals("So does this quoted scalar.\n", map.get("quoted"));
    }
}
