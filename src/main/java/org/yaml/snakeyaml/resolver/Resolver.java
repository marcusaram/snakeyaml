/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.resolver;

import org.yaml.snakeyaml.nodes.Node;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Resolver {
    // TODO index or object ???
    public void descendResolver(Node currentNode, Object currentIndex);

    public void ascendResolver();

    public String resolve(Class<? extends Node> class1, String value, boolean implicit);
}
