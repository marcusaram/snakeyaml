/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

/**
 * @see PyYAML for more information
 */
public abstract class CollectionNode extends Node {
    private boolean flowStyle;

    public CollectionNode(final String tag, final Object value, final boolean flowStyle) {
        super(tag, value);
        this.flowStyle = flowStyle;
    }

    public boolean getFlowStyle() {
        return flowStyle;
    }
}
