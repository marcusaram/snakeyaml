/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import org.yaml.snakeyaml.Yaml;

import junit.framework.TestCase;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 */
public class YAMLDumpTest extends TestCase {
    private Yaml yaml;

    @Override
    protected void setUp() throws Exception {
        yaml = new Yaml();
    }

    public void testBasicStringDump() {
        assertEquals("--- str\n", yaml.dump("str"));
    }

    public void testBasicHashDump() {
        Map ex = new HashMap();
        ex.put("a", "b");
        assertEquals("--- \na: b\n", yaml.dump(ex));
    }

    public void testBasicListDump() {
        List ex = new ArrayList();
        ex.add("a");
        ex.add("b");
        ex.add("c");
        assertEquals("--- \n- a\n- b\n- c\n", yaml.dump(ex));
    }

    public void testVersionDumps() {
        Yaml yaml = new Yaml(new DefaultYAMLConfig().explicitTypes(true));
        assertEquals("--- !!int 1\n", yaml.dump(new Integer(1)));
        yaml = new Yaml(new DefaultYAMLConfig().version("1.0").explicitTypes(true));
        assertEquals("--- !int 1\n", yaml.dump(new Integer(1)));
    }

    public void testMoreScalars() {
        assertEquals("--- !!str 1.0\n", yaml.dump("1.0"));
    }

    public void testDumpJavaBean() {
        final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("CET"));
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean toDump = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                "--- !java/object:org.jvyaml.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-02T22:00:00Z\n",
                yaml.dump(toDump));

    }

    public void testEmitLongString() throws IOException {
        String str = "xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx\n\n";
        java.io.StringWriter w = new java.io.StringWriter();
        new EmitterImpl(w, new DefaultYAMLConfig()).writeDoubleQuoted(str, true);
    }
}
