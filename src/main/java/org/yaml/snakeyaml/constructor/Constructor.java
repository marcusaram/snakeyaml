/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public class Constructor extends SafeConstructor {

    public Constructor() {
        super();
        // TODO this.yamlMultiConstructors.put("!java/object:", new
        // ConstuctYamlNull());
    }

    private class ConstuctJavaBean implements Construct {
        public Object construct(Node node) {
            constructScalar(node);
            return null;
        }
    }

}
