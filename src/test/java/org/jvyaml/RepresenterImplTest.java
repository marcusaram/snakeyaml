package org.jvyaml;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class RepresenterImplTest extends TestCase {

    public void testRepresenterImpl() throws IOException {
        main(new String[0]);
    }

    public static void main(final String[] args) throws IOException {
        final YamlConfig cfg = new DefaultYAMLConfig();
        final Serializer s = new SerializerImpl(new EmitterImpl(new java.io.OutputStreamWriter(
                System.out), cfg), new ResolverImpl(), cfg);
        s.open();
        final Representer r = new RepresenterImpl(s, cfg);
        final Map test1 = new HashMap();
        final List test1Val = new LinkedList();
        test1Val.add("hello");
        test1Val.add(Boolean.TRUE);
        test1Val.add(new Integer(31337));
        test1.put("val1", test1Val);
        final List test2Val = new ArrayList();
        test2Val.add("hello");
        test2Val.add(Boolean.FALSE);
        test2Val.add(new Integer(31337));
        test1.put("val2", test2Val);
        test1.put("afsdf", "hmm");
        TestJavaBean bean1 = new TestJavaBean();
        bean1.setName("Ola");
        bean1.setSurName("Bini");
        bean1.setAge(24);
        test1.put(new Integer(25), bean1);
        r.represent(test1);
        s.close();
    }

    private static class TestJavaBean implements Serializable {
        private String val1;
        private String val2;
        private int val3;

        public TestJavaBean() {
        }

        public void setName(final String name) {
            this.val1 = name;
        }

        public String getName() {
            return this.val1;
        }

        public void setSurName(final String sname) {
            this.val2 = sname;
        }

        public String getSurName() {
            return this.val2;
        }

        public void setAge(final int age) {
            this.val3 = age;
        }

        public int getAge() {
            return this.val3;
        }
    }
}
