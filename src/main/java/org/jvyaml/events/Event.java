/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

import org.jvyaml.Mark;

/**
 * @see PyYAML 3.06 for more information
 */
public abstract class Event {
    private Mark startMark;
    private Mark endMark;

    public Event(final Mark startMark, final Mark endMark) {
        this.startMark = startMark;
        this.endMark = endMark;
    }

    public String toString() {
        return "<" + this.getClass().getName() + "(" + getArguments() + ")>";
    }

    public Mark getStartMark() {
        return startMark;
    }

    public Mark getEndMark() {
        return endMark;
    }

    /**
     * @see __repr__ for Event in PyYAML
     */
    protected String getArguments() {
        return "";
    }
}
