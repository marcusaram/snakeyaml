/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Parser {
    boolean checkEvent(final Class[] choices);

    Event peekEvent();

    Event getEvent();

    void parseStream();

    Event parseStreamNext();
}
