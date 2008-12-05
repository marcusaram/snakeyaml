package org.yaml.snakeyaml.constructor;

import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class ConstructorTest extends TestCase {

    public void testGetSingleData() {
        String data = "american:\n  - Boston Red Sox";
        Object map = construct(data);
        assertNotNull(map);
        assertTrue(map.getClass().toString(), map instanceof Map);
        assertTrue(map.getClass().toString(), map instanceof HashMap);
        // TODO assertTrue(map.getClass().toString(), map instanceof
        // LinkedHashMap);
    }

    public void testGetBean() {
        String data = "--- !java/object:org.yaml.snakeyaml.constructor.Person\nfirstName: Andrey\nage: 99";
        Object obj = construct(data);
        assertNotNull(obj);
        assertTrue(obj.getClass().toString(), obj instanceof Person);
        Person person = (Person) obj;
        assertEquals("Andrey", person.getFirstName());
        assertNull(person.getLastName());
        assertEquals(99, person.getAge());
    }

    /**
     * TODO Deviation from PyYAML. Assume the tag is the class name
     */
    public void qtestGetBeanAssumeClass() {
        String data = "--- !org.yaml.snakeyaml.constructor.Person\nfirstName: Andrey\nage: 99";
        Object obj = construct(data);
        assertNotNull(obj);
        assertTrue("Unexpected: " + obj.getClass().toString(), obj instanceof Person);
        Person person = (Person) obj;
        assertEquals("Andrey", person.getFirstName());
        assertNull(person.getLastName());
        assertEquals(99, person.getAge());
    }

    // TODO sequence constructor is not yet implemented
    /**
     * create instance from constructor
     */
    public void qtestGetConstructorBean() {
        String data = "--- !java/object:org.yaml.snakeyaml.constructor.Person [ Andrey, Somov, 99 ]";
        Object obj = construct(data);
        assertNotNull(obj);
        assertTrue(obj.getClass().toString(), obj instanceof Person);
        Person person = (Person) obj;
        assertEquals("Andrey", person.getFirstName());
        assertNull(person.getLastName());
        assertEquals(99, person.getAge());
    }

    private Object construct(String data) {
        Reader reader = new Reader(data);
        Scanner scanner = new ScannerImpl(reader);
        Parser parser = new ParserImpl(scanner);
        Resolver resolver = new Resolver();
        Composer composer = new Composer(parser, resolver);
        Constructor constructor = new Constructor();
        constructor.setComposer(composer);
        Object result = constructor.getSingleData();
        return result;
    }
}
