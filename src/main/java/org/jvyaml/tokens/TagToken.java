/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.tokens;

/**
 * @see PyYAML 3.06 for more information
 */
public class TagToken extends Token {
    private String[] value;

    public TagToken(final String[] value) {
        this.value = value;
    }

    public String[] getValue() {
        return this.value;
    }
}
