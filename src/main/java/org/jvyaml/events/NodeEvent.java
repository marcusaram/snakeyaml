/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public abstract class NodeEvent extends Event {
    private String anchor;
    public NodeEvent(final String anchor) {
        this.anchor = anchor;
    }

    public String getAnchor() {
        return this.anchor;
    }
}// NodeEvent
