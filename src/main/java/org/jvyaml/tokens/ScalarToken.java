/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @see PyYAML for more information
 */
public class ScalarToken extends Token {
    private String value;
    private boolean plain;
    private char style;

    public ScalarToken(final String value, final boolean plain) {
        this(value,plain,(char)0);
    }

    public ScalarToken(final String value, final boolean plain, final char style) {
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

    public String toString() {
        return "#<" + this.getClass().getName() + " value=\"" + value + "\">";
    }
}// ScalarToken
