/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class CollectionNode extends Node {
    private boolean flowStyle;

    // TODO should value be a collection ? py4fun
    public CollectionNode(final String tag, final Object value, final Mark startMark,
            final Mark endMark, final boolean flowStyle) {
        super(tag, value, startMark, endMark);
        this.flowStyle = flowStyle;
    }

    public boolean getFlowStyle() {
        return flowStyle;
    }
}
