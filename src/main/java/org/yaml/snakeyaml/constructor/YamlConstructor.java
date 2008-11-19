/**
 * 
 */
package org.yaml.snakeyaml.constructor;

import org.yaml.snakeyaml.nodes.Node;

public interface YamlConstructor {
    Object call(final Constructor self, final Node node);
}