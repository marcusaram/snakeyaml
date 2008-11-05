/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class ScalarToken extends Token {
    private String value;
    private boolean plain;
    private char style;

    public ScalarToken(final String value, final Mark startMark, final Mark endMark,
            final boolean plain) {
        this(value, plain, startMark, endMark, (char) 0);
    }

    public ScalarToken(final String value, final boolean plain, final Mark startMark,
            final Mark endMark, final char style) {
        super(startMark, endMark);
        this.value = value;
        this.plain = plain;
        this.style = style;
    }

    public boolean getPlain() {
        return this.plain;
    }

    public String getValue() {
        return this.value;
    }

    public char getStyle() {
        return this.style;
    }

    @Override
    protected String getArguments() {
        return "value=" + value + ", plain=" + plain + ", style=" + style;
    }

    @Override
    public String getTokenId() {
        return "<scalar>";
    }
}
