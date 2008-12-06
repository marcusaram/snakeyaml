/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.parser.ParserException;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 */
public class YAMLLoadTest extends TestCase {
    private Yaml yaml;

    @Override
    protected void setUp() throws Exception {
        yaml = new Yaml();
    }

    public void testBasicStringScalarLoad() {
        assertEquals("str", yaml.load("--- str"));
        assertEquals("str", yaml.load("---\nstr"));
        assertEquals("str", yaml.load("--- \nstr"));
        assertEquals("str", yaml.load("--- \n str"));
        assertEquals("str", yaml.load("str"));
        assertEquals("str", yaml.load(" str"));
        assertEquals("str", yaml.load("\nstr"));
        assertEquals("str", yaml.load("\n str"));
        assertEquals("str", yaml.load("\"str\""));
        assertEquals("str", yaml.load("'str'"));
    }

    public void testBasicIntegerScalarLoad() {
        assertEquals(new Long(47), yaml.load("47"));
        assertEquals(new Long(0), yaml.load("0"));
        assertEquals(new Long(-1), yaml.load("-1"));
    }

    public void testBlockMappingLoad() {
        Map expected = new HashMap();
        expected.put("a", "b");
        expected.put("c", "d");
        assertEquals(expected, yaml.load("a: b\nc: d"));
        assertEquals(expected, yaml.load("c: d\na: b\n"));
    }

    public void testFlowMappingLoad() {
        Map expected = new HashMap();
        expected.put("a", "b");
        expected.put("c", "d");
        assertEquals(expected, yaml.load("{a: b, c: d}"));
        assertEquals(expected, yaml.load("{c: d,\na: b}"));
    }

    public void testBuiltinTag() {
        assertEquals("str", yaml.load("!!str str"));
        assertEquals("str", yaml.load("%YAML 1.1\n---\n!!str str"));
    }

    public void testDirectives() {
        assertEquals("str", yaml.load("%YAML 1.1\n--- !!str str"));
        assertEquals("str", yaml
                .load("%YAML 1.1\n%TAG !yaml! tag:yaml.org,2002:\n--- !yaml!str str"));
        try {
            yaml.load("%YAML 1.1\n%YAML 1.1\n--- !!str str");
            fail("should throw exception when repeating directive");
        } catch (final ParserException e) {
            assertTrue(true);
        }
    }

    // TODO fix test
    public void qtestJavaBeanLoad() {
        final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean expected = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                expected,
                yaml
                        .load("--- !java/object:org.jvyaml.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-03\n"));
    }
}
