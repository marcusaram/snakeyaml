/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

import java.util.List;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Resolver {
    // TODO index or object ???
    public void descendResolver(final Node currentNode, final Object currentIndex);

    public void ascendResolver();

    public boolean checkResolverPrefix(final int depth, final List path, final Class kind,
            final Node currentNode, final Object currentIndex);

    public String resolve(final Class<? extends Node> class1, final String value,
            final boolean[] implicit);
}
