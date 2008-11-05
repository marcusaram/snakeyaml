/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class AliasToken extends Token {
    private String value;

    public AliasToken(final String value, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        this.value = value;
    }

    public String getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        return "value=" + value;
    }

    @Override
    public String getTokenId() {
        return "<alias>";
    }
}
