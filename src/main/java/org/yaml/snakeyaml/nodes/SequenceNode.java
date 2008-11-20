/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class SequenceNode extends CollectionNode {
    public static final String id = "sequence";

    @SuppressWarnings("unchecked")
    public SequenceNode(final String tag, final List value, final Mark startMark,
            final Mark endMark, final Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
