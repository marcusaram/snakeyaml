/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface YAMLNodeCreator {
    String taguri();
    Node toYamlNode(Representer representer) throws IOException;
}
