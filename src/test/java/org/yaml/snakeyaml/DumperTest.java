package org.yaml.snakeyaml;

import java.io.IOException;
import java.io.Writer;
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

    public void testDumpException() {
        Yaml yaml = new Yaml();
        Writer writer = new ExceptionWriter1();
        try {
            yaml.dump("aaa1234567890", writer);
            System.out.println(writer.toString());
            fail("Exception must be thrown.");
        } catch (Exception e) {
            assertEquals("java.io.IOException: write test failure.", e.getMessage());
        }
    }

    private class ExceptionWriter1 extends Writer {
        @Override
        public void write(String str) throws IOException {
            throw new IOException("write test failure.");
        }

        @Override
        public void close() throws IOException {
        }

        @Override
        public void flush() throws IOException {
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }
    }

    public void testDumpCloseException() {
        Yaml yaml = new Yaml();
        Writer writer = new ExceptionWriter2();
        try {
            yaml.dump("aaa1234567890", writer);
            System.out.println(writer.toString());
            fail("Exception must be thrown.");
        } catch (Exception e) {
            assertEquals("java.io.IOException: close test failure.", e.getMessage());
        }
    }

    private class ExceptionWriter2 extends Writer {
        @Override
        public void flush() throws IOException {
            throw new IOException("close test failure.");
        }

        @Override
        public void close() throws IOException {
            throw new IOException("close test failure.");
        }

        @Override
        public void write(char[] cbuf, int off, int len) throws IOException {
        }
    }
}
