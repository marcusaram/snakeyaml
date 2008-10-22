/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @see PyYAML for more information
 */
public abstract class NodeEvent extends Event {
    private String anchor;

    public NodeEvent(final String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return this.anchor;
    }
}
