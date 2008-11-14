/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class Token {
    private Mark startMark;
    private Mark endMark;

    public Token(final Mark startMark, final Mark endMark) {
        if (startMark == null || endMark == null) {
            throw new NullPointerException("Marks in a Token are required.");
        }
        this.startMark = startMark;
        this.endMark = endMark;
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
