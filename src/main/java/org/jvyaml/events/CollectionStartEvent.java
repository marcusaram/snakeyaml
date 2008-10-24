/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import org.jvyaml.Mark;

/**
 * @see PyYAML for more information
 */
public abstract class CollectionStartEvent extends NodeEvent {
    private String tag;
    private boolean implicit;
    private boolean flowStyle;

    public CollectionStartEvent(final String anchor, final String tag, final boolean implicit,
            final Mark startMark, final Mark endMark, final boolean flowStyle) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        this.flowStyle = flowStyle;
    }

    public String getTag() {
        return this.tag;
    }

    public boolean getImplicit() {
        return this.implicit;
    }

    public boolean getFlowStyle() {
        return this.flowStyle;
    }
}
