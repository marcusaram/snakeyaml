/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface Serializer {
    void open() throws IOException;

    void close() throws IOException;

    void serialize(final Node node) throws IOException;
}
