/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import java.util.Map;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class DocumentStartEvent extends Event {
    private boolean explicit;
    private int[] version;
    private Map<String, String> tags;

    public DocumentStartEvent(final Mark startMark, final Mark endMark, final boolean explicit,
            final int[] version, final Map<String, String> tags) {
        super(startMark, endMark);
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

    public Map<String, String> getTags() {
        return tags;
    }
}
