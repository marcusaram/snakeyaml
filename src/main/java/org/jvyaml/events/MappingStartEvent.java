/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class MappingStartEvent extends CollectionStartEvent {
    public MappingStartEvent(final String anchor, final String tag, final boolean implicit, final boolean flowStyle) {
        super(anchor,tag,implicit,flowStyle);
    }
}// MappingStartEvent
