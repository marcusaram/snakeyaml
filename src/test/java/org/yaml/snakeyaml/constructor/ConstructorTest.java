package org.yaml.snakeyaml.constructor;

import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.TimeZone;

import junit.framework.TestCase;

import org.yaml.snakeyaml.composer.Composer;
import org.yaml.snakeyaml.parser.Parser;
import org.yaml.snakeyaml.parser.ParserImpl;
import org.yaml.snakeyaml.reader.Reader;
import org.yaml.snakeyaml.resolver.Resolver;
import org.yaml.snakeyaml.scanner.Scanner;
import org.yaml.snakeyaml.scanner.ScannerImpl;

public class ConstructorTest extends TestCase {

    @SuppressWarnings("unchecked")
    public void testMapOrder() {
        String data = "one: zzz\ntwo: ccc\nthree: bbb\nfour: aaa";
        Object map = construct(data);
        assertNotNull(map);
        assertTrue(map.getClass().toString(), map instanceof LinkedHashMap);
        Map<String, String> m = (Map<String, String>) map;
        assertEquals(4, m.keySet().size());
        Iterator<String> iter = m.keySet().iterator();
        assertEquals("one", iter.next());
        assertEquals("two", iter.next());
        assertEquals("three", iter.next());
        assertEquals("four", iter.next());
    }

    // TODO fix test
    public void qtestGetBean() {
        String data = "--- !java/object:org.yaml.snakeyaml.constructor.Person\nfirstName: Andrey\nage: 99";
        Object obj = construct(data);
        assertNotNull(obj);
        assertTrue(obj.getClass().toString(), obj instanceof Person);
        Person person = (Person) obj;
        assertEquals("Andrey", person.getFirstName());
        assertNull(person.getLastName());
        assertEquals(99, person.getAge());
    }

    // TODO fix test
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

    /**
     * create instance from constructor
     */
    // TODO fix test
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

    // TODO fix test
    public void qtestJavaBeanLoad() {
        final java.util.Calendar cal = java.util.Calendar.getInstance(TimeZone.getTimeZone("UTC"));
        cal.clear();
        cal.set(1982, 5 - 1, 3); // Java's months are zero-based...

        final TestBean expected = new TestBean("Ola Bini", 24, cal.getTime());
        assertEquals(
                expected,
                construct("--- !java/object:org.jvyaml.TestBean\nname: Ola Bini\nage: 24\nborn: 1982-05-03\n"));
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
