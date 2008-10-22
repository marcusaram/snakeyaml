/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public abstract class CollectionStartEvent extends NodeEvent {
    private String tag;
    private boolean implicit;
    private boolean flowStyle;

    public CollectionStartEvent(final String anchor, final String tag, final boolean implicit, final boolean flowStyle) {
        super(anchor);
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
}// CollectionStartEvent
