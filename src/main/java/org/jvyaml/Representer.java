/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import java.util.List;
import java.util.Map;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface Representer {
    void represent(final Object data) throws IOException;
    Node scalar(final String tag, final String value, char style) throws IOException;
    Node seq(final String tag, final List sequence, final boolean flowStyle) throws IOException;
    Node map(final String tag, final Map mapping, final boolean flowStyle) throws IOException;
}// Representer
