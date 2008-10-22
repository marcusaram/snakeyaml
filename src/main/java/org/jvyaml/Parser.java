/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.util.Iterator;

import org.jvyaml.events.Event;

/**
 * @see PyYAML for more information
 */
public interface Parser {
    boolean checkEvent(final Class[] choices);

    Event peekEvent();

    Event getEvent();

    Iterator eachEvent();

    Iterator iterator();

    void parseStream();

    Event parseStreamNext();
}
