/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.jvyaml.TestBean;
import org.yaml.snakeyaml.Yaml;

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
        assertEquals("str\n", yaml.dump("str"));
    }

    public void testBasicHashDump() {
        Map ex = new HashMap();
        ex.put("a", "b");
        assertEquals("a: b\n", yaml.dump(ex));
    }

    public void testBasicListDump() {
        List ex = new ArrayList();
        ex.add("a");
        ex.add("b");
        ex.add("c");
        assertEquals("- a\n- b\n- c\n", yaml.dump(ex));
    }

    public void testDumpJavaBean() {
        final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("CET"));
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean toDump = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                "!java/object:org.jvyaml.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-02T22:00:00Z\n",
                yaml.dump(toDump));

    }

}
