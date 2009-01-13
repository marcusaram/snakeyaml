/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;

public class RepresenterTest extends TestCase {

    public void testRepresenter() {
        MyBean bean = new MyBean();
        bean.setName("Gnome");
        bean.setValid(true);
        Yaml yaml = new Yaml();
        assertEquals(
                "!!org.yaml.snakeyaml.representer.RepresenterTest$MyBean {name: Gnome, valid: true}\n",
                yaml.dump(bean));
    }

    private class MyBean {
        private String name;
        private Boolean valid;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Boolean isValid() {
            return valid;
        }

        public void setValid(Boolean valid) {
            this.valid = valid;
        }
    }

    public void testRepresenterNoConstructorAvailable() {
        MyBean2 bean = new MyBean2("Gnome", true);
        Yaml yaml = new Yaml();
        assertEquals("!!org.yaml.snakeyaml.representer.RepresenterTest$MyBean2 {valid: true}\n",
                yaml.dump(bean));
    }

    private class MyBean2 {
        private String name;
        private Boolean valid;

        public MyBean2(String name, Boolean valid) {
            this();
            this.name = name;
            this.valid = valid;
        }

        private MyBean2() {
            super();
        }

        private String getName() {
            return name;
        }

        public Boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return getName() + " " + isValid();
        }
    }

    public void testRepresenterGetterWithException() {
        MyBean3 bean = new MyBean3("Gnome", true);
        Yaml yaml = new Yaml();
        assertEquals(
                "!!org.yaml.snakeyaml.representer.RepresenterTest$MyBean3 {name: null, valid: true}\n",
                yaml.dump(bean));
    }

    private class MyBean3 {
        private String name;
        private Boolean valid;

        public MyBean3(String name, Boolean valid) {
            this.name = name;
            this.valid = valid;
        }

        public String getName() {
            throw new UnsupportedOperationException("Test.");
        }

        public Boolean isValid() {
            return valid;
        }

        @Override
        public String toString() {
            return name + " " + isValid();
        }
    }
}
