/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;

public class ImplicitTagsTest extends TestCase {

    public void testDefaultRepresenter() throws IOException {
        CarWithWheel car = new CarWithWheel();
        car.setPlate("12-XP-F4");
        Wheel wheel = new Wheel();
        wheel.setId(2);
        car.setWheel(wheel);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", 3);
        car.setMap(map);
        car.setPart(new Wheel(4));
        assertEquals(Util.getLocalResource("constructor/carwheel-without-tags.yaml"), new Yaml()
                .dump(car));
    }

    public void testRootMap() throws IOException {
        Map<Object, Object> car = new HashMap<Object, Object>();
        car.put("plate", "12-XP-F4");
        Wheel wheel = new Wheel();
        wheel.setId(2);
        car.put("wheel", wheel);
        Map<String, Integer> map = new HashMap<String, Integer>();
        map.put("id", 3);
        car.put("map", map);
        assertEquals(Util.getLocalResource("constructor/carwheel-root-map.yaml"), new Yaml()
                .dump(car));
    }

    public void testLoadClassTag() throws IOException {
        Constructor constructor = new Constructor();
        constructor.addTypeDefinition(new TypeDescription(Car.class, "!car"));
        Loader loader = new Loader(constructor);
        Yaml yaml = new Yaml(loader);
        Car car = (Car) yaml.load(Util.getLocalResource("constructor/car-without-tags.yaml"));
        assertEquals("12-XP-F4", car.getPlate());
        List<Wheel> wheels = car.getWheels();
        assertNotNull(wheels);
        assertEquals(5, wheels.size());
    }

    public static class CarWithWheel {
        private String plate;
        private Wheel wheel;
        private Object part;
        private Map<String, Integer> map;

        public String getPlate() {
            return plate;
        }

        public void setPlate(String plate) {
            this.plate = plate;
        }

        public Wheel getWheel() {
            return wheel;
        }

        public void setWheel(Wheel wheel) {
            this.wheel = wheel;
        }

        public Map<String, Integer> getMap() {
            return map;
        }

        public void setMap(Map<String, Integer> map) {
            this.map = map;
        }

        public Object getPart() {
            return part;
        }

        public void setPart(Object part) {
            this.part = part;
        }
    }
}
