/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import junit.framework.TestCase;

public class EnumTest extends TestCase {
    public void testDumpEnum() {
        Yaml yaml = new Yaml();
        String output = yaml.dump(Suit.CLUBS);
        assertEquals("!!org.yaml.snakeyaml.Suit 'CLUBS'\n", output);
    }

    public void testDumpEnumArray() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        Yaml yaml = new Yaml(options);
        String output = yaml.dump(Suit.values());
        assertEquals(
                "- !!org.yaml.snakeyaml.Suit 'CLUBS'\n- !!org.yaml.snakeyaml.Suit 'DIAMONDS'\n- !!org.yaml.snakeyaml.Suit 'HEARTS'\n- !!org.yaml.snakeyaml.Suit 'SPADES'\n",
                output);
    }

    public void testDumpEnumList() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        Yaml yaml = new Yaml(options);
        List<Suit> list = Arrays.asList(Suit.values());
        String output = yaml.dump(list);
        assertEquals(
                "- !!org.yaml.snakeyaml.Suit 'CLUBS'\n- !!org.yaml.snakeyaml.Suit 'DIAMONDS'\n- !!org.yaml.snakeyaml.Suit 'HEARTS'\n- !!org.yaml.snakeyaml.Suit 'SPADES'\n",
                output);
    }

    public void testDumpEnumMap() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        Yaml yaml = new Yaml(options);
        Map<String, Suit> map = new LinkedHashMap<String, Suit>();
        map.put("c", Suit.CLUBS);
        map.put("d", Suit.DIAMONDS);
        String output = yaml.dump(map);
        assertEquals(
                "c: !!org.yaml.snakeyaml.Suit 'CLUBS'\nd: !!org.yaml.snakeyaml.Suit 'DIAMONDS'\n",
                output);
    }

}
