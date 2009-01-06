/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.io.IOException;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.constructor.Construct;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.MappingNode;
import org.yaml.snakeyaml.nodes.Node;

/**
 * Test Example 2.27 from the YAML specification
 * 
 * @author py4fun
 * @see http://yaml.org/spec/1.1/
 */
public class Example2_27Test extends TestCase {
    class MyConstructor extends Constructor {
        public MyConstructor() {
            this.yamlConstructors.put("tag:clarkevans.com,2002:invoice", new ConstructInvoice());
        }

        private class ConstructInvoice implements Construct {
            @SuppressWarnings("unchecked")
            public Object construct(Node node) {
                MappingNode mnode = (MappingNode) node;
                Map values = (Map) constructMapping(mnode);
                Integer id = (Integer) values.get("invoice");
                String date = (String) values.get("date");
                Person bill_to = (Person) values.get("bill-to");
                Person ship_to = (Person) values.get("ship-to");
                Product[] product = (Product[]) values.get("product");
                Float tax = (Float) values.get("tax");
                Float total = (Float) values.get("total");
                String comments = (String) values.get("comments");
                Invoice invoice = new Invoice(id, date, bill_to, ship_to, product, tax, total,
                        comments);
                return invoice;
            }
        }

    }

    private class Invoice {
        private Integer id; // invoice
        private String date; // date
        private Person billTo;// bill-to
        private Person shipTo;// ship-to
        private Product[] product;
        private Float tax;
        private Float total;
        private String comments;

        public Invoice(Integer id, String date, Person billTo, Person shipTo, Product[] product,
                Float tax, Float total, String comments) {
            this.id = id;
            this.date = date;
            this.billTo = billTo;
            this.shipTo = shipTo;
            this.product = product;
            this.tax = tax;
            this.total = total;
            this.comments = comments;
        }

        public Integer getId() {
            return id;
        }

        public String getDate() {
            return date;
        }

        public Person getBillTo() {
            return billTo;
        }

        public Person getShipTo() {
            return shipTo;
        }

        public Product[] getProduct() {
            return product;
        }

        public Float getTax() {
            return tax;
        }

        public Float getTotal() {
            return total;
        }

        public String getComments() {
            return comments;
        }
    }

    private class Person {
        private String given;
        private String family;
        private Address address;
    }

    private class Address {
        private String lines;
        private String city;
        private String state;
        private String postal;
    }

    private class Product {
        private String sku;
        private Integer quantity;
        private String description;
        private Float price;
    }

    public void testStub() throws IOException {
        // TODO finish 2.27 example
    }

    public void qtestExample_2_27() throws IOException {
        Loader loader = new Loader(new MyConstructor());
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
