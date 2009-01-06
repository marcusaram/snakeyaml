/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import java.util.TimeZone;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.TestBean;

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

    public void testDumpJavaBean() {
        final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("CET"));
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean toDump = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                "!!org.yaml.snakeyaml.constructor.TestBean {age: 24, born: !!timestamp '1982-05-02T22:00:00Z',\n  name: Ola Bini}\n",
                yaml.dump(toDump));

    }

}
