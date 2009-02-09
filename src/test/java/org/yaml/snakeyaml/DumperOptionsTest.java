package org.yaml.snakeyaml;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class DumperOptionsTest extends TestCase {

    public void testDefaultStyle() {
        DumperOptions options = new DumperOptions();
        Yaml yaml = new Yaml(options);
        assertEquals("'123'\n", yaml.dump("123"));
        //
        options.setDefaultStyle('\"');
        yaml = new Yaml(options);
        assertEquals("\"123\"\n", yaml.dump("123"));
        //
        options.setDefaultStyle('\'');
        yaml = new Yaml(options);
        assertEquals("'123'\n", yaml.dump("123"));
        //
        options.setDefaultStyle(null);
        yaml = new Yaml(options);
        assertEquals("'123'\n", yaml.dump("123"));
    }

    public void testDefaultFlowStyle() {
        Yaml yaml = new Yaml();
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        assertEquals("[1, 2, 3]\n", yaml.dump(list));
        //
        DumperOptions options = new DumperOptions();
        options = new DumperOptions();
        options.setDefaultFlowStyle(true);
        yaml = new Yaml(options);
        assertEquals("[1, 2, 3]\n", yaml.dump(list));
        //
        options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        yaml = new Yaml(options);
        assertEquals("- 1\n- 2\n- 3\n", yaml.dump(list));
    }

    public void testDefaultFlowStyleNested() {
        Yaml yaml = new Yaml();
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        list.add(3);
        Map<String, Object> map = new LinkedHashMap<String, Object>();
        map.put("a", "b");
        map.put("c", list);
        assertEquals("a: b\nc: [1, 2, 3]\n", yaml.dump(map));
        //
        DumperOptions options = new DumperOptions();
        options = new DumperOptions();
        options.setDefaultFlowStyle(true);
        yaml = new Yaml(options);
        assertEquals("{a: b, c: [1, 2, 3]}\n", yaml.dump(map));
        //
        options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        yaml = new Yaml(options);
        assertEquals("a: b\nc:\n- 1\n- 2\n- 3\n", yaml.dump(map));
    }

    public void testCanonical() {
        Yaml yaml = new Yaml();
        assertEquals("123\n", yaml.dump(123));
        //
        DumperOptions options = new DumperOptions();
        options = new DumperOptions();
        options.setCanonical(true);
        yaml = new Yaml(options);
        assertEquals("---\n!!int \"123\"\n", yaml.dump(123));
        //
        options = new DumperOptions();
        options.setCanonical(false);
        yaml = new Yaml(options);
        assertEquals("123\n", yaml.dump(123));
    }

    public void testIndent() {
        Yaml yaml = new Yaml();
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        DumperOptions options = new DumperOptions();
        options.setCanonical(true);
        yaml = new Yaml(options);
        assertEquals("---\n!!seq [\n  !!int \"1\",\n  !!int \"2\",\n]\n", yaml.dump(list));
        //
        options.setIndent(4);
        yaml = new Yaml(options);
        assertEquals("---\n!!seq [\n    !!int \"1\",\n    !!int \"2\",\n]\n", yaml.dump(list));
    }

    public void testLineBreak() {
        Yaml yaml = new Yaml();
        List<Integer> list = new LinkedList<Integer>();
        list.add(1);
        list.add(2);
        DumperOptions options = new DumperOptions();
        options.setCanonical(true);
        yaml = new Yaml(options);
        assertEquals("---\n!!seq [\n  !!int \"1\",\n  !!int \"2\",\n]\n", yaml.dump(list));
        //
        options.setLineBreak("\r\n");
        yaml = new Yaml(options);
        assertEquals("---\r\n!!seq [\r\n  !!int \"1\",\r\n  !!int \"2\",\r\n]\r\n", yaml.dump(list));
    }
}
