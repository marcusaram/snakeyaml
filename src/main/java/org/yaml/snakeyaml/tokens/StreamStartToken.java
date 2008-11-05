/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import java.nio.charset.Charset;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class StreamStartToken extends Token {
    private Charset encoding;

    public StreamStartToken(Mark startMark, Mark endMark, Charset encoding) {
        super(startMark, endMark);
        this.encoding = encoding;
    }

    @Override
    public String getTokenId() {
        return "<stream start>";
    }

    public Charset getEncoding() {
        return encoding;
    }
}
