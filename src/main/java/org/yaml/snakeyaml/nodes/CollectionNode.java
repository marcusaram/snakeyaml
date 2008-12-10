/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML for more information
 */
public abstract class CollectionNode extends Node {
    private Boolean flowStyle;

    public CollectionNode(final String tag, final Object value, final Mark startMark,
            final Mark endMark, final Boolean flowStyle) {
        super(tag, value, startMark, endMark);
        this.flowStyle = flowStyle;
    }

    public Boolean getFlowStyle() {
        return flowStyle;
    }

    public void setFlowStyle(Boolean flowStyle) {
        this.flowStyle = flowStyle;
    }

    public void setEndMark(Mark endMark) {
        this.endMark = endMark;
    }
}
