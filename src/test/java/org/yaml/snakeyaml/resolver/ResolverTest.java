package org.yaml.snakeyaml.resolver;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.nodes.ScalarNode;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ResolverTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testAddImplicitResolver() {
        Dumper dumper = new Dumper(new MyRepresenter(), new DumperOptions());
        Loader loader = new Loader(new MyConstructor());
        Yaml yaml = new Yaml(loader, dumper);
        Pattern regexp = Pattern.compile("\\d\\d-\\d\\d-\\d\\d\\d");
        yaml.addImplicitResolver("tag:yaml.org,2002:Phone", regexp, "0123456789");
        Phone phone1 = new Phone("12-34-567");
        Phone phone2 = new Phone("11-22-333");
        Phone phone3 = new Phone("44-55-777");
        List<Phone> etalonList = new LinkedList<Phone>();
        etalonList.add(phone1);
        etalonList.add(phone2);
        etalonList.add(phone3);
        String output = yaml.dump(etalonList);
        assertEquals("[12-34-567, 11-22-333, 44-55-777]\n", output);
        List<Phone> parsedList = (List<Phone>) yaml.load(output);
        assertEquals(3, parsedList.size());
        assertEquals(phone1, parsedList.get(0));
        assertEquals(phone2, parsedList.get(1));
        assertEquals(phone3, parsedList.get(2));
        assertEquals(etalonList, parsedList);
    }

    class Phone {
        private String number;

        public Phone(String n) {
            this.number = n;
        }

        public String getNumber() {
            return number;
        }

        @Override
        public boolean equals(Object obj) {
            if (!(obj instanceof Phone)) {
                return false;
            }
            return toString().equals(obj.toString());
        }

        @Override
        public String toString() {
            return "Phone: " + number;
        }
    }

    class MyRepresenter extends Representer {
        public MyRepresenter() {
            this.representers.put(Phone.class, new RepresentPhone());
        }

        private class RepresentPhone implements Represent {
            public Node representData(Object data) {
                Phone phone = (Phone) data;
                String value = phone.getNumber();
                return representScalar("tag:yaml.org,2002:Phone", value);
            }
        }
    }

    class MyConstructor extends Constructor {
        public MyConstructor() {
            this.yamlConstructors.put("tag:yaml.org,2002:Phone", new ConstuctPhone());
        }

        private class ConstuctPhone implements Construct {
            @SuppressWarnings("unchecked")
            public <T> T construct(Class<T> clazz, Node node) {
                String val = (String) constructScalar((ScalarNode) node);
                return (T) new Phone(val);
            }
        }
    }
}
