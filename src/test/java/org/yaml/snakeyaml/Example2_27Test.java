/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Test Example 2.27 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Example2_27Test extends TestCase {
    class ClassConstructor extends Constructor {

        public ClassConstructor() {
            this.rootClass = Invoice.class;
        }

        private class ConstuctYamlObject implements Construct {
            public <T> T construct(Class<T> clazz, Node node) {
                if (clazz.equals(Invoice.class)) {

                } else {
                    throw new UnsupportedOperationException();
                }
                T result = null;
                return result;
            }
        }

        private Object delegate(Node node) {
            return constructObject(Object.class, node);
        }
    }

    private class Invoice {
        public Integer id; // invoice
        public String date; // date
        public Person billTo;// bill-to
        public Person shipTo;// ship-to
        public List<Product> products;
        public Float tax;
        public Float total;
        public String comments;

        public Invoice(Integer id, String date, Person billTo, Person shipTo,
                List<Product> product, Float tax, Float total, String comments) {
            this.id = id;
            this.date = date;
            this.billTo = billTo;
            this.shipTo = shipTo;
            this.products = product;
            this.tax = tax;
            this.total = total;
            this.comments = comments;
        }
    }

    private class Person {
        public String given;
        public String family;
        public Address address;
    }

    private class Address {
        public String lines;
        public String city;
        public String state;
        public String postal;
    }

    private class Product {
        public String sku;
        public Integer quantity;
        public String description;
        public Float price;
    }

    public void testStub() throws IOException {
        // TODO finish 2.27 example
    }

    public void qtestExample_2_27() throws IOException {
        Loader loader = new Loader(new ClassConstructor());
        Yaml yaml = new Yaml(loader);
        Invoice invoice = (Invoice) yaml.load(Util
                .getLocalResource("specification/example2_27.yaml"));
        assertNotNull(invoice);
        // Dumper dumper = new Dumper(new MyRepresenter(), new DumperOptions());
        // yaml = new Yaml(dumper);
        // String output = yaml.dump(invoice);
        // String etalon =
        // Util.getLocalResource("specification/example2_27_dumped.yaml");
        // assertEquals(etalon, output);
    }
}
