package org.yaml.snakeyaml;

import junit.framework.TestCase;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Yaml;

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
}
