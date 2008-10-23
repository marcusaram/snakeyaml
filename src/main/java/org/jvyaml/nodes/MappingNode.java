/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import java.util.Map;

/**
 * @see PyYAML for more information
 */
public class MappingNode extends CollectionNode {
    public static final String id = "mapping";

    public MappingNode(final String tag, final Map value, final boolean flowStyle) {
        super(tag, value, flowStyle);
    }

    @Override
    public String getNodeId() {
        return id;
    }
}
