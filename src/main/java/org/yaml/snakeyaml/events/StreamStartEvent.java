/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import java.nio.charset.Charset;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class StreamStartEvent extends Event {
    private Charset encoding;

    public StreamStartEvent(final Mark startMark, final Mark endMark, final Charset encoding) {
        super(startMark, endMark);
        this.encoding = encoding;
    }

    public StreamStartEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.encoding = Charset.forName("UTF-8");
    }

    public Charset getEncoding() {
        return encoding;
    }
}
