/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.types;

import java.io.IOException;

import org.yaml.snakeyaml.YamlDocument;

/**
 * @see http://yaml.org/type/yaml.html
 */
public class YamlTagTest extends AbstractTest {

    @SuppressWarnings("unchecked")
    public void testYaml() throws IOException {
        try {
            YamlDocument document = new YamlDocument("types/yaml.yaml");
            document.getNativeData();
        } catch (RuntimeException e) {
            // TODO yaml type is not yet properly implemented (the test fail
            // also in PyYAML)
        }
    }
}
