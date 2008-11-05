/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import java.nio.charset.Charset;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class Token {
    public final static Token STREAM_END = new StreamEndToken(null, null);
    public final static Token STREAM_START = new StreamStartToken(null, null, Charset
            .forName("UTF-8"));

    private Mark startMark;
    private Mark endMark;

    public Token(final Mark startMark, final Mark endMark) {
        this.startMark = null;
        this.endMark = null;
    }

    public String toString() {
        return "<" + this.getClass().getName() + "(" + getArguments() + ")>";
    }

    public Mark getStartMark() {
        return startMark;
    }

    public Mark getEndMark() {
        return endMark;
    }

    /**
     * @see __repr__ for Token in PyYAML
     */
    protected String getArguments() {
        return "";
    }

    /**
     * For error reporting.
     * 
     * @see class variable 'id' in PyYAML
     */
    public abstract String getTokenId();

}
