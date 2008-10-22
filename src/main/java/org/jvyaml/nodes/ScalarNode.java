/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.nodes;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.2 $
 */
public class ScalarNode extends Node {
    private char style;
    public ScalarNode(final String tag, final String value, final char style) {
        super(tag,value);
        this.style = style;
    }

    public char getStyle() {
        return style;
    }
}// ScalarNode
