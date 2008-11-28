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
    private Character style;

    public ScalarNode(String tag, String value, Mark startMark, Mark endMark, Character style) {
        super(tag, value, startMark, endMark);
        this.style = style;
    }

    public ScalarNode(String tag, String value) {
        super(tag, value, null, null);
        this.style = null;
    }

    public Character getStyle() {
        return style;
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
