/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.nodes;

import java.util.List;
import java.util.Map;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class MappingNode extends CollectionNode {
    public static final String id = "mapping";

    @SuppressWarnings("unchecked")
    // TODO MAp must be list !!!
    public MappingNode(String tag, Map value, Mark startMark, Mark endMark, Boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
    }

    public MappingNode(String tag, List<Object[]> value, Boolean flowStyle) {
        super(tag, value, null, null, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
