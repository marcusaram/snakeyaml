/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import org.jvyaml.Mark;
import org.jvyaml.YAMLException;

/**
 * @see PyYAML 3.06 for more information
 */
public class DirectiveToken extends Token {
    private String name;
    private String[] value;

    public DirectiveToken(final String name, final String[] value, final Mark startMark,
            final Mark endMark) {
        super(startMark, endMark);
        this.name = name;
        if (value.length != 2) {
            throw new YAMLException("Two strings must be provided instead of "
                    + String.valueOf(value.length));
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public String[] getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        return "name=" + name + ", value=[" + value[0] + ", " + value[1] + "]";
    }

    @Override
    public String getTokenId() {
        return "<directive>";
    }
}
