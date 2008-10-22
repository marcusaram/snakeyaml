/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.List;

import org.jvyaml.nodes.Node;

/**
 * @see PyYAML for more information
 */
public interface Resolver {
    void descendResolver(final Node currentNode, final Object currentIndex);

    void ascendResolver();

    boolean checkResolverPrefix(final int depth, final List path, final Class kind,
            final Node currentNode, final Object currentIndex);

    String resolve(final Class kind, final String value, final boolean[] implicit);
}
