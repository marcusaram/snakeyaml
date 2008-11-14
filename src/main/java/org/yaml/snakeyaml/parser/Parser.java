/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.parser;

import java.util.Iterator;

import org.yaml.snakeyaml.events.Event;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Parser {
    boolean checkEvent(final Class[] choices);

    boolean checkEvent(Class choice);

    Event peekEvent();

    Event getEvent();

    Iterator eachEvent();

    Iterator iterator();

    void parseStream();

    Event parseStreamNext();
}
