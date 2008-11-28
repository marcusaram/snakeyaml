/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.events;

import org.yaml.snakeyaml.error.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public class ScalarEvent extends NodeEvent {
    private String tag;
    private Character style;
    private String value;
    private boolean[] implicit;

    public ScalarEvent(String anchor, String tag, boolean[] implicit, String value, Mark startMark,
            Mark endMark, Character style) {
        super(anchor, startMark, endMark);
        this.tag = tag;
        this.implicit = implicit;
        this.value = value;
        this.style = style;
    }

    public String getTag() {
        return this.tag;
    }

    public Character getStyle() {
        return this.style;
    }

    public String getValue() {
        return this.value;
    }

    public boolean[] getImplicit() {
        return this.implicit;
    }

    @Override
    protected String getArguments() {
        return super.getArguments() + ", tag=" + tag + ", implicit=[" + implicit[0] + ", "
                + implicit[1] + "], value=" + value;
    }

}
