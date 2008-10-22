/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public abstract class Event {
    public String toString() {
        return "#<" + this.getClass().getName() + ">";
    }
}// Event
