/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class DocumentEndEvent extends Event {
    private boolean explicit;

    public DocumentEndEvent(final Mark startMark, final Mark endMark, final boolean explicit) {
        super(startMark, endMark);
        this.explicit = explicit;
    }

    public boolean getExplicit() {
        return explicit;
    }
}
