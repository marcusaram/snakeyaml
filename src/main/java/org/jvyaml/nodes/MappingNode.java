/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

import java.util.Map;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class MappingNode extends CollectionNode {
    public MappingNode(final String tag, final Map value, final boolean flowStyle) {
        super(tag,value,flowStyle);
    }
}// MappingNode
