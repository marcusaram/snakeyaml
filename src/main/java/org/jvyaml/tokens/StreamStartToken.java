/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

import org.jvyaml.Mark;
import org.jvyaml.events.StreamStartEvent.Encoding;

/**
 * @see PyYAML 3.06 for more information
 */
public class StreamStartToken extends Token {
    private Encoding encoding;

    public StreamStartToken(Mark startMark, Mark endMark, Encoding encoding) {
        super(startMark, endMark);
        this.encoding = encoding;
    }

    @Override
    public String getTokenId() {
        return "<stream start>";
    }

    public Encoding getEncoding() {
        return encoding;
    }
}
