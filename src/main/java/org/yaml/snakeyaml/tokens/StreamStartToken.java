/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import java.nio.charset.Charset;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
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

    /**
     * @see __repr__ for Token in PyYAML
     */
    protected String getArguments() {
        return "encoding=" + encoding;
    }

}
