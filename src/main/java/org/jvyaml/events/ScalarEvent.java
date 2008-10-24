/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import org.jvyaml.Mark;

/**
 * @see PyYAML for more information
 */
public class ScalarEvent extends NodeEvent {
    private String tag;
    private char style;
    private String value;
    private boolean[] implicit;

    public ScalarEvent(final String anchor, final String tag, final boolean[] implicit,
            final String value, final Mark startMark, final Mark endMark, final char style) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        this.value = value;
        this.style = style;
    }

    public String getTag() {
        return this.tag;
    }

    public char getStyle() {
        return this.style;
    }

    public String getValue() {
        return this.value;
    }

    public boolean[] getImplicit() {
        return this.implicit;
    }

    public String toString() {
        return "#<" + this.getClass().getName() + " value=\"" + value + "\">";
    }
}
