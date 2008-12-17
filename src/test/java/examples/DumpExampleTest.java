package examples;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;

public class DumpExampleTest extends TestCase {
    public void testDump() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "Silenthand Olleander");
        data.put("race", "Human");
        data.put("traits", new String[] { "ONE_HAND", "ONE_EYE" });
        Yaml yaml = new Yaml();
        String output = yaml.dump(data);
        System.out.println(output);
        assertTrue(output.contains("name: Silenthand Olleander"));
        assertTrue(output.contains("race: Human"));
        assertTrue(output.contains("traits: [ONE_HAND, ONE_EYE]"));
    }

    public void testDumpWriter() {
        Map<String, Object> data = new HashMap<String, Object>();
        data.put("name", "Silenthand Olleander");
        data.put("race", "Human");
        data.put("traits", new String[] { "ONE_HAND", "ONE_EYE" });
        Yaml yaml = new Yaml();
        StringWriter writer = new StringWriter();
        yaml.dump(data, writer);
        System.out.println(writer.toString());
        assertTrue(writer.toString().contains("name: Silenthand Olleander"));
        assertTrue(writer.toString().contains("race: Human"));
        assertTrue(writer.toString().contains("traits: [ONE_HAND, ONE_EYE]"));
    }

    public void testDumpMany() {
        List<Integer> docs = new LinkedList<Integer>();
        for (int i = 0; i < 3; i++) {
            docs.add(i);
        }
        Yaml yaml = new Yaml();
        String output = yaml.dumpAll(docs.iterator());
        System.out.println(output);
    }
}
