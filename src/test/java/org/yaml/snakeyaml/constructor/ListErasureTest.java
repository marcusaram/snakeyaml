/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Dumper;
import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.nodes.Node;
import org.yaml.snakeyaml.representer.Represent;
import org.yaml.snakeyaml.representer.Representer;

public class ListErasureTest extends TestCase {

    public void testDefaultRepresenter() throws IOException {
        Car car = new Car();
        car.setPlate("12-XP-F4");
        List<Wheel> wheels = new LinkedList<Wheel>();
        for (int i = 1; i < 6; i++) {
            Wheel wheel = new Wheel();
            wheel.setId(i);
            wheels.add(wheel);
        }
        car.setWheels(wheels);
        assertEquals(Util.getLocalResource("constructor/car-with-tags.yaml"), new Yaml().dump(car));
        Dumper dumper = new Dumper(new MyRepresenter(), new DumperOptions());
        Yaml yaml = new Yaml(dumper);
        String output = yaml.dump(car);
        assertEquals(Util.getLocalResource("constructor/car-without-tags.yaml"), output);
        Car car2 = (Car) yaml.load(output);
        assertNotNull(car2);
        assertEquals("12-XP-F4", car2.getPlate());
        List<Wheel> wheels2 = car2.getWheels();
        assertNotNull(wheels2);
        assertEquals(5, wheels2.size());
        // for (Wheel wheel : wheels2) {
        // System.out.println(wheel);
        // assertTrue(wheel.getId() > 0);
        // assertTrue(wheel.getId() < 6);
        // }
    }

    private class MyRepresenter extends Representer {
        Represent defaultRepresenter;

        public MyRepresenter() {
            super(null, Boolean.FALSE);
            defaultRepresenter = this.representers.get(null);
            this.representers.put(null, new RepresentCar());
        }

        private class RepresentCar implements Represent {
            @SuppressWarnings("unchecked")
            public Node representData(Object data) {
                if (data instanceof Wheel) {
                    Wheel wheel = (Wheel) data;
                    Map values = new HashMap();
                    values.put("id", wheel.getId());
                    return representMapping("tag:yaml.org,2002:map", values, null);
                } else {
                    return defaultRepresenter.representData(data);
                }
            }
        }

    }
}
