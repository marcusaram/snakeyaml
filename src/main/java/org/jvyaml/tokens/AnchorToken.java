/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @see PyYAML for more information
 */
public class AnchorToken extends Token {
    private String value;

    public AnchorToken() {
        super();
    }

    public AnchorToken(final String value) {
        this.value = value;
    }

    public void setValue(final Object value) {
        this.value = (String) value;
    }

    public String getValue() {
        return this.value;
    }
}
