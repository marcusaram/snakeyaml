/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import java.util.Map;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class DocumentStartEvent extends Event {
    private boolean explicit;
    private int[] version;
    private Map tags;

    public DocumentStartEvent(final boolean explicit, final int[] version, final Map tags) {
        this.explicit = explicit;
        this.version = version;
        this.tags = tags;
    }

    public boolean getExplicit() {
        return explicit;
    }

    public int[] getVersion() {
        return version;
    }

    public Map getTags() {
        return tags;
    }
}// DocumentStartEvent
