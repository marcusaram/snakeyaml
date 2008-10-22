/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import java.util.List;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class SequenceNode extends CollectionNode {
    public SequenceNode(final String tag, final List value, final boolean flowStyle) {
        super(tag,value,flowStyle);
    }
}// SequenceNode
