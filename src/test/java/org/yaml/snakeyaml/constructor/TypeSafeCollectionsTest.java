/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import java.io.IOException;
import java.util.List;

import junit.framework.TestCase;

import org.yaml.snakeyaml.ClassDescription;
import org.yaml.snakeyaml.Loader;
import org.yaml.snakeyaml.Util;
import org.yaml.snakeyaml.Yaml;

public class TypeSafeCollectionsTest extends TestCase {

    public void testTypeSafeList() throws IOException {
        Constructor constructor = new Constructor(Car.class);
        ClassDescription carDescription = new ClassDescription(Car.class);
        carDescription.putListPropertyType("wheels", Wheel.class);
        constructor.addClassDefinition(carDescription);
        Loader loader = new Loader(constructor);
        Yaml yaml = new Yaml(loader);
        Car car = (Car) yaml.load(Util.getLocalResource("constructor/car-no-root-class.yaml"));
        assertEquals("12-XP-F4", car.getPlate());
        List<Wheel> wheels = car.getWheels();
        assertNotNull(wheels);
        assertEquals(5, wheels.size());
        for (Wheel wheel : wheels) {
            assertTrue(wheel.getId() > 0);
        }
    }
}
