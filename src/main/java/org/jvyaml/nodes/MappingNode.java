/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class MappingNode extends CollectionNode {
    public static final String id = "mapping";

    public MappingNode(final String tag, final Object value, final Mark startMark,
            final Mark endMark, final boolean flowStyle) {
        super(tag, value, startMark, endMark, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
