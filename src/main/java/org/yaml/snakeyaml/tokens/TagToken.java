/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import org.jvyaml.Mark;
import org.jvyaml.YAMLException;

/**
 * @see PyYAML 3.06 for more information
 */
public class TagToken extends Token {
    private String[] value;

    public TagToken(final String[] value, final Mark startMark, final Mark endMark) {
        super(startMark, endMark);
        if (value.length != 2) {
            throw new YAMLException("Two strings must be provided instead of "
                    + String.valueOf(value.length));
        }
        this.value = value;
    }

    public String[] getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        return "value=[" + value[0] + ", " + value[1] + "]";
    }

    @Override
    public String getTokenId() {
        return "<tag>";
    }
}
