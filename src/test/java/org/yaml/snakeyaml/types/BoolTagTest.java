/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import junit.framework.TestCase;

import org.jvyaml.YAML;

/**
 * @see http://yaml.org/type/int.html
 */
public class BoolTagTest extends TestCase {
    @SuppressWarnings("unchecked")
    private Map<String, Boolean> getData(String data) {
        Map<String, Boolean> nativeData = (Map<String, Boolean>) YAML.load(data);
        return nativeData;
    }

    public void testBool() throws IOException {
        assertEquals(Boolean.TRUE, getData("canonical: true").get("canonical"));
        assertEquals(Boolean.FALSE, getData("answer: NO").get("answer"));
        assertEquals(Boolean.TRUE, getData("logical: True").get("logical"));
        assertEquals(Boolean.TRUE, getData("option: on").get("option"));
    }

    public void testBoolCanonical() throws IOException {
        assertEquals(Boolean.TRUE, getData("canonical: Yes").get("canonical"));
        assertEquals(Boolean.TRUE, getData("canonical: yes").get("canonical"));
        assertEquals(Boolean.TRUE, getData("canonical: YES").get("canonical"));
        assertEquals("yES", getData("canonical: yES").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: No").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: NO").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: no").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: off").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: Off").get("canonical"));
        assertEquals(Boolean.FALSE, getData("canonical: OFF").get("canonical"));
        assertEquals(Boolean.TRUE, getData("canonical: ON").get("canonical"));
        assertEquals(Boolean.TRUE, getData("canonical: On").get("canonical"));
        // TODO assertEquals(Boolean.TRUE, getData("canonical:
        // Y").get("canonical"));
        // TODO assertEquals(Boolean.TRUE, getData("canonical:
        // y").get("canonical"));
    }

    public void testBoolShorthand() throws IOException {
        Map<String, Boolean> nativeData = (Map<String, Boolean>) getData("boolean: !!bool true");
        assertEquals(Boolean.TRUE, nativeData.get("boolean"));
    }

    public void testBoolTag() throws IOException {
        Map<String, Boolean> nativeData = (Map<String, Boolean>) getData("boolean: !<tag:yaml.org,2002:bool> true");
        assertEquals(Boolean.TRUE, nativeData.get("boolean"));
    }

    public void testBoolOut() throws IOException {
        Map<String, Boolean> map = new HashMap<String, Boolean>();
        map.put("boolean", Boolean.TRUE);
        String output = YAML.dump(map);
        assertTrue(output.contains("boolean: true"));
    }

}
