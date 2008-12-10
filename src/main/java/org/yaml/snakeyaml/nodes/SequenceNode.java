/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML for more information
 */
public class SequenceNode extends CollectionNode {
    public static final String id = "sequence";

    public SequenceNode(String tag, List<Node> value, Mark startMark, Mark endMark,
            Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
    }

    public SequenceNode(String tag, List<Node> value, Boolean flowStyle) {
        super(tag, value, null, null, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public List<Node> getValue() {
        return (List<Node>) super.getValue();
    }
}
