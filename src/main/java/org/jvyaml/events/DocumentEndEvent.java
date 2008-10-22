/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
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
