/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.tokens;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;
import org.yaml.snakeyaml.error.YAMLException;

/**
 * @see PyYAML 3.06 for more information
 */
public class DirectiveToken extends Token {
    private String name;
    private List<?> value;

    public DirectiveToken(final String name, final List<?> value, final Mark startMark,
            final Mark endMark) {
        super(startMark, endMark);
        this.name = name;
        if (value.size() != 2) {
            throw new YAMLException("Two strings must be provided instead of "
                    + String.valueOf(value.size()));
        }
        this.value = value;
    }

    public String getName() {
        return this.name;
    }

    public List<?> getValue() {
        return this.value;
    }

    @Override
    protected String getArguments() {
        return "name=" + name + ", value=[" + value.get(0) + ", " + value.get(1) + "]";
    }

    @Override
    public String getTokenId() {
        return "<directive>";
    }
}
