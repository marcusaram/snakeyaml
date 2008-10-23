/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import java.util.List;

/**
 * @see PyYAML for more information
 */
public class SequenceNode extends CollectionNode {
    public static final String id = "sequence";

    public SequenceNode(final String tag, final List value, final boolean flowStyle) {
        super(tag, value, flowStyle);
    }
}
