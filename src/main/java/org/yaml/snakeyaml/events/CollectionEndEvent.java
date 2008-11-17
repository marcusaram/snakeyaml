/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class CollectionEndEvent extends Event {

    public CollectionEndEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
    }
}
