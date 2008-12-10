/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML for more information
 */
public class MappingEndEvent extends CollectionEndEvent {

    public MappingEndEvent(Mark startMark, Mark endMark) {
        super(startMark, endMark);
    }
}
