/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class ScalarNode extends Node {
    public static final String id = "scalar";
    private char style;

    public ScalarNode(final String tag, final String value, final Mark startMark,
            final Mark endMark, final char style) {
        super(tag, value, startMark, endMark);
        this.style = style;
    }

    public char getStyle() {
        return style;
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
