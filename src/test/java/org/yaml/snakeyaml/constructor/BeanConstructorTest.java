package org.yaml.snakeyaml.constructor;

import java.io.IOException;
import java.math.BigInteger;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;

public class BeanConstructorTest extends TestCase {

    public void testPrimitivesConstructor() throws IOException {
        Loader loader = new Loader(new BeanConstructor(TestBean1.class));
        Yaml yaml = new Yaml(loader);
        String document = Util.getLocalResource("constructor/test-primitives1.yaml");
        System.out.println(document);
        TestBean1 result = (TestBean1) yaml.load(document);
        assertNotNull(result);
        assertEquals(new Byte((byte) 1), result.getByteClass());
        assertEquals((byte) -3, result.getBytePrimitive());
        assertEquals(new Short((short) 0), result.getShortClass());
        assertEquals((short) -13, result.getShortPrimitive());
        assertEquals(new Integer(5), result.getInteger());
        assertEquals(17, result.getIntPrimitive());
        assertEquals("the text", result.getText());
        assertEquals("13", result.getId());
        assertEquals(new Long(11111111111L), result.getLongClass());
        assertEquals(9999999999L, result.getLongPrimitive());
        assertEquals(Boolean.TRUE, result.getBooleanClass());
        assertTrue(result.isBooleanPrimitive());
        assertEquals(new Character('2'), result.getCharClass());
        assertEquals('#', result.getCharPrimitive());
        assertEquals(new BigInteger("1234567890123456789012345678901234567890"), result
                .getBigInteger());
    }
}
