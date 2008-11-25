package org.yaml.snakeyaml.serializer;

import java.text.NumberFormat;

import junit.framework.TestCase;

public class SerializerTest extends TestCase {

    public void testSerialize() {
        // TODO fail("Not yet implemented");
    }

    public void testGenerateAnchor() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        String anchor = format.format(3L);
        assertEquals("003", anchor);
    }

}
