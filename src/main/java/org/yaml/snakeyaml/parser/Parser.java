/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.parser;

import java.util.List;

import org.yaml.snakeyaml.events.Event;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Parser {
    public boolean checkEvent(List<Class<? extends Event>> choices);

    public boolean checkEvent(Class<? extends Event> choice);

    public Event peekEvent();

    public Event getEvent();

}
