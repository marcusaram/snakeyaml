package org.yaml.snakeyaml.serializer;

import java.io.IOException;
import java.io.StringWriter;
import java.text.NumberFormat;

import junit.framework.TestCase;

import org.yaml.snakeyaml.YamlConfig;
import org.yaml.snakeyaml.emitter.Emitter;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.resolver.Resolver;

public class SerializerTest extends TestCase {
    private Serializer serializer;

    @Override
    protected void setUp() throws Exception {
        YamlConfig config = new YamlConfig();
        StringWriter writer = new StringWriter();
        serializer = new Serializer(new Emitter(writer, config), new Resolver(), config);
    }

    public void testSerializerIsAlreadyOpened() throws IOException {
        serializer.open();
        try {
            serializer.open();
            fail();
        } catch (RuntimeException e) {
            assertEquals("serializer is already opened", e.getMessage());
        }
    }

    public void testSerializerIsClosed1() throws IOException {
        serializer.open();
        serializer.close();
        try {
            serializer.open();
            fail();
        } catch (RuntimeException e) {
            assertEquals("serializer is closed", e.getMessage());
        }
    }

    public void testSerializerIsClosed2() throws IOException {
        serializer.open();
        serializer.close();
        try {
            serializer.serialize(new ScalarNode("!foo", "bar", null, null, (char) 0));
            fail();
        } catch (RuntimeException e) {
            assertEquals("serializer is closed", e.getMessage());
        }
    }

    public void testSerializerIsNotOpened1() throws IOException {
        try {
            serializer.close();
            fail();
        } catch (RuntimeException e) {
            assertEquals("serializer is not opened", e.getMessage());
        }
    }

    public void testSerializerIsNotOpened2() throws IOException {
        try {
            serializer.serialize(new ScalarNode("!foo", "bar", null, null, (char) 0));
            fail();
        } catch (RuntimeException e) {
            assertEquals("serializer is not opened", e.getMessage());
        }
    }

    public void testGenerateAnchor() {
        NumberFormat format = NumberFormat.getNumberInstance();
        format.setMinimumIntegerDigits(3);
        String anchor = format.format(3L);
        assertEquals("003", anchor);
    }
}
