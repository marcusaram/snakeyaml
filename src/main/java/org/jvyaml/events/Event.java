/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @see PyYAML for more information
 */
public abstract class Event {
    public String toString() {
        return "#<" + this.getClass().getName() + ">";
    }
}// Event
