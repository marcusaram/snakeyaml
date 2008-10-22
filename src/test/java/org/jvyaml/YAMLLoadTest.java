/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 */
public class YAMLLoadTest extends TestCase {
    public YAMLLoadTest(final String name) {
        super(name);
    }

    public void testBasicStringScalarLoad() {
        assertEquals("str", YAML.load("--- str"));
        assertEquals("str", YAML.load("---\nstr"));
        assertEquals("str", YAML.load("--- \nstr"));
        assertEquals("str", YAML.load("--- \n str"));
        assertEquals("str", YAML.load("str"));
        assertEquals("str", YAML.load(" str"));
        assertEquals("str", YAML.load("\nstr"));
        assertEquals("str", YAML.load("\n str"));
        assertEquals("str", YAML.load("\"str\""));
        assertEquals("str", YAML.load("'str'"));
    }

    public void testBasicIntegerScalarLoad() {
        assertEquals(new Long(47), YAML.load("47"));
        assertEquals(new Long(0), YAML.load("0"));
        assertEquals(new Long(-1), YAML.load("-1"));
    }

    public void testBlockMappingLoad() {
        Map expected = new HashMap();
        expected.put("a", "b");
        expected.put("c", "d");
        assertEquals(expected, YAML.load("a: b\nc: d"));
        assertEquals(expected, YAML.load("c: d\na: b\n"));
    }

    public void testFlowMappingLoad() {
        Map expected = new HashMap();
        expected.put("a", "b");
        expected.put("c", "d");
        assertEquals(expected, YAML.load("{a: b, c: d}"));
        assertEquals(expected, YAML.load("{c: d,\na: b}"));
    }

    public void testBuiltinTag() {
        assertEquals("str", YAML.load("!!str str"));
        assertEquals("str", YAML.load("%YAML 1.1\n---\n!!str str"));
        assertEquals("str", YAML.load("%YAML 1.0\n---\n!str str"));
        assertEquals("str", YAML.load("---\n!str str", YAML.config().version("1.0")));
        assertEquals(new Long(123), YAML.load("---\n!int 123", YAML.config().version("1.0")));
        assertEquals(new Long(123), YAML.load("%YAML 1.1\n---\n!!int 123", YAML.config().version(
                "1.0")));
    }

    public void testDirectives() {
        assertEquals("str", YAML.load("%YAML 1.1\n--- !!str str"));
        assertEquals("str", YAML
                .load("%YAML 1.1\n%TAG !yaml! tag:yaml.org,2002:\n--- !yaml!str str"));
        try {
            YAML.load("%YAML 1.1\n%YAML 1.1\n--- !!str str");
            fail("should throw exception when repeating directive");
        } catch (final ParserException e) {
            assertTrue(true);
        }
    }

    public void testJavaBeanLoad() {
        final java.util.Calendar cal = java.util.Calendar.getInstance();
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean expected = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                expected,
                YAML
                        .load("--- !java/object:org.jvyaml.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-03\n"));
    }
}
