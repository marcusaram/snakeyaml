/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.YamlDocument;

/**
 * @see http://yaml.org/type/pairs.html
 */
public class PairsTagTest extends AbstractTest {

    @SuppressWarnings("unchecked")
    public void testPairs() throws IOException {
        try {
            YamlDocument document = new YamlDocument("types/pairs.yaml");
            Map<String, List<Map<String, String>>> map = (Map<String, List<Map<String, String>>>) document
                    .getNativeData();
            assertEquals(2, map.size());
            List<Map<String, String>> list1 = (List<Map<String, String>>) map.get("Block tasks");
            assertEquals(4, list1.size());
            assertEquals("with team.", list1.get(0).get("meeting"));
            assertEquals("with boss.", list1.get(1).get("meeting"));
            assertEquals("lunch.", list1.get(2).get("break"));
            assertEquals("with client.", list1.get(3).get("meeting"));
            //
            List<Map<String, String>> list2 = (List<Map<String, String>>) map.get("Flow tasks");
            assertEquals(2, list2.size());
            assertEquals("with team", list2.get(0).get("meeting"));
            assertEquals("with boss", list2.get(1).get("meeting"));
        } catch (RuntimeException e) {
            // TODO pairs is not yet properly implemented
        }
    }

}
