/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

/**
 * @see PyYAML for more information
 */
public class ScalarNode extends Node {
    public static final String id = "scalar";
    private char style;

    public ScalarNode(final String tag, final String value, final char style) {
        super(tag, value);
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
