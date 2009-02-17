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
    // Dumping
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

    public void testDumpEnumBean() {
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(false);
        Yaml yaml = new Yaml(options);
        EnumBean bean = new EnumBean();
        bean.setId(17);
        bean.setSuit(Suit.SPADES);
        String output = yaml.dump(bean);
        assertEquals("!!org.yaml.snakeyaml.EnumBean\nid: 17\nsuit: SPADES\n", output);
    }

    // Loading
    public void testLoadEnum() {
        Yaml yaml = new Yaml();
        Suit suit = (Suit) yaml.load("!!org.yaml.snakeyaml.Suit 'CLUBS'\n");
        assertEquals(Suit.CLUBS, suit);
    }

    @SuppressWarnings("unchecked")
    public void testLoadEnumList() {
        Yaml yaml = new Yaml();
        List<Suit> list = (List<Suit>) yaml
                .load("- !!org.yaml.snakeyaml.Suit 'CLUBS'\n- !!org.yaml.snakeyaml.Suit 'DIAMONDS'\n- !!org.yaml.snakeyaml.Suit 'HEARTS'\n- !!org.yaml.snakeyaml.Suit 'SPADES'");
        assertEquals(4, list.size());
        assertEquals(Suit.CLUBS, list.get(0));
        assertEquals(Suit.DIAMONDS, list.get(1));
        assertEquals(Suit.HEARTS, list.get(2));
        assertEquals(Suit.SPADES, list.get(3));
    }

    @SuppressWarnings("unchecked")
    public void testLoadEnumMap() {
        Yaml yaml = new Yaml();
        Map<Integer, Suit> map = (Map<Integer, Suit>) yaml
                .load("1: !!org.yaml.snakeyaml.Suit 'HEARTS'\n2: !!org.yaml.snakeyaml.Suit 'DIAMONDS'");
        assertEquals(2, map.size());
        assertEquals(Suit.HEARTS, map.get(1));
        assertEquals(Suit.DIAMONDS, map.get(2));
    }

    public void testLoadEnumBean() {
        Yaml yaml = new Yaml();
        EnumBean bean = (EnumBean) yaml.load("!!org.yaml.snakeyaml.EnumBean\nid: 174\nsuit: CLUBS");
        assertEquals(Suit.CLUBS, bean.getSuit());
        assertEquals(174, bean.getId());
    }
}
