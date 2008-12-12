package examples;

import java.util.List;

import junit.framework.TestCase;

import org.yaml.snakeyaml.Yaml;

public class LoadExampleTest extends TestCase {
    @SuppressWarnings("unchecked")
    public void testLoad() {
        Yaml yaml = new Yaml();
        String document = "\n- Hesperiidae\n- Papilionidae\n- Apatelodidae\n- Epiplemidae";
        System.out.println(document);
        List<String> list = (List<String>) yaml.load(document);
        System.out.println(list);
        assertEquals("[Hesperiidae, Papilionidae, Apatelodidae, Epiplemidae]", list.toString());
    }

}
