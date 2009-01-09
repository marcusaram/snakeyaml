package org.yaml.snakeyaml.constructor;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;

public class BeanConstructorTest extends TestCase {

    public void testPrimitivesConstructor() {
        Loader loader = new Loader(new BeanConstructor(TestBean1.class));
        Yaml yaml = new Yaml(loader);
        String document = "integer: 5\ntext: the text\nintPrimitive: 17\nid: 13";
        TestBean1 result = (TestBean1) yaml.load(document);
        assertNotNull(result);
        assertEquals("the text", result.getText());
        assertEquals(new Integer(5), result.getInteger());
        assertEquals(17, result.getIntPrimitive());
        assertEquals("13", result.getId());
    }
}
