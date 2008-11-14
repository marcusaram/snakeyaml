/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.parser;

import org.yaml.snakeyaml.events.Event;
import org.yaml.snakeyaml.tokens.Token;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Parser {
    boolean checkEvent(final Class<Token>[] choices);

    Event peekEvent();

    Event getEvent();

    void parseStream();

    Event parseStreamNext();
}
