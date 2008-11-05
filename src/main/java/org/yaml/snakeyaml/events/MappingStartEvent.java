/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class MappingStartEvent extends CollectionStartEvent {
    public MappingStartEvent(final String anchor, final String tag, final boolean implicit,
            final Mark startMark, final Mark endMark, final boolean flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }
}
