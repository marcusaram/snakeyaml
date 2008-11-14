/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class CollectionNode extends Node {
    private boolean flowStyle;

    public CollectionNode(final String tag, final Object value, final Mark startMark,
            final Mark endMark, final boolean flowStyle) {
        super(tag, value, startMark, endMark);
        this.flowStyle = flowStyle;
    }

    public boolean getFlowStyle() {
        return flowStyle;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}
