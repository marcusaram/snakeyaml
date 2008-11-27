/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class ScalarNode extends Node {
    public static final String id = "scalar";
    private char style;

    public ScalarNode(String tag, String value, Mark startMark, Mark endMark, char style) {
        super(tag, value, startMark, endMark);
        this.style = style;
    }

    public ScalarNode(String tag, String value) {
        super(tag, value, null, null);
        this.style = 0;
    }

    public char getStyle() {
        return style;
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
