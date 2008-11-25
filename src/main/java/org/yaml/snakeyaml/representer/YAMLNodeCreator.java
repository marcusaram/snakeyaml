/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.representer;

import java.io.IOException;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public interface YAMLNodeCreator {
    String taguri();

    Node toYamlNode(Representer representer) throws IOException;
}
