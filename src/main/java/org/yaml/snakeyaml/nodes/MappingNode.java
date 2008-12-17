/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see <a href="http://pyyaml.org/wiki/PyYAML">PyYAML</a> for more information
 */
public class MappingNode extends CollectionNode {
    public static final String id = "mapping";

    public MappingNode(String tag, List<Node[]> value, Mark startMark, Mark endMark,
            Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
    }

    public MappingNode(String tag, List<Node[]> value, Boolean flowStyle) {
        super(tag, value, null, null, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }

    @SuppressWarnings("unchecked")
    public List<Node[]> getValue() {
        List<Node[]> mapping = (List<Node[]>) super.getValue();
        return mapping;
    }

    public void setValue(List<Node[]> merge) {
        value = merge;
    }
}
