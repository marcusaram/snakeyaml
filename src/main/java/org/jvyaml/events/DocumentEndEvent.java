/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @see PyYAML for more information
 */
public class DocumentEndEvent extends Event {
    private boolean explicit;

    public DocumentEndEvent(final boolean explicit) {
        this.explicit = explicit;
    }

    public boolean getExplicit() {
        return explicit;
    }
}// DocumentEndEvent
