/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import org.jvyaml.Mark;

/**
 * @see PyYAML for more information
 */
public class MappingStartEvent extends CollectionStartEvent {
    public MappingStartEvent(final String anchor, final String tag, final boolean implicit,
            final Mark startMark, final Mark endMark, final boolean flowStyle) {
        super(anchor, tag, implicit, startMark, endMark, flowStyle);
    }
}
