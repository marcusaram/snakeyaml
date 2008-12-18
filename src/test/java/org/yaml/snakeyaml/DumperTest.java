package org.yaml.snakeyaml;

import java.util.LinkedList;
import java.util.List;

import junit.framework.TestCase;

public class DumperTest extends TestCase {

    public void testDump1() {
        DumperOptions options = new DumperOptions();
        options.setDefaultStyle('"');
        options.explicitStart(true);
        options.explicitEnd(true);
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(list);
        assertEquals("---\n- !!int \"0\"\n- !!int \"1\"\n- !!int \"2\"\n...\n", output);
    }

    public void testDump2() {
        DumperOptions options = new DumperOptions();
        options.explicitStart(true);
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(list);
        assertEquals("--- [0, 1, 2]\n", output);
    }

    public void testDump3() {
        DumperOptions options = new DumperOptions();
        options.setDefaultStyle('\'');
        List<Integer> list = new LinkedList<Integer>();
        for (int i = 0; i < 3; i++) {
            list.add(i);
        }
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(list);
        assertEquals("- !!int '0'\n- !!int '1'\n- !!int '2'\n", output);
    }

}
