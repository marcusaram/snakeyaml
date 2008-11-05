/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class CollectionEndEvent extends Event {

    public CollectionEndEvent() {
        super(null, null);
    }
}
