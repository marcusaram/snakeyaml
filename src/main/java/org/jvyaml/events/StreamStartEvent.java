/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class StreamStartEvent extends Event {
    public enum Encoding {
        UTF8, UTF16LE, UTF16BE
    }

    private Encoding encoding;

    public StreamStartEvent(final Mark startMark, final Mark endMark, final Encoding encoding) {
        super(startMark, endMark);
        this.encoding = encoding;
    }

    public StreamStartEvent(final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.encoding = Encoding.UTF8;
    }

    public Encoding getEncoding() {
        return encoding;
    }
}
