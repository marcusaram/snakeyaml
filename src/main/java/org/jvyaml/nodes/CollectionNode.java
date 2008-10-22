/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class CollectionNode extends Node {
    private boolean flowStyle;
    public CollectionNode(final String tag, final Object value, final boolean flowStyle) {
        super(tag,value);
        this.flowStyle = flowStyle;
    }

    public boolean getFlowStyle() {
        return flowStyle;
    }
}// CollectionNode
