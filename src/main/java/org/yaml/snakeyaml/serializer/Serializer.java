/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.serializer;

import java.io.IOException;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Serializer {
    void open() throws IOException;

    void close() throws IOException;

    void serialize(Node node) throws IOException;
}
