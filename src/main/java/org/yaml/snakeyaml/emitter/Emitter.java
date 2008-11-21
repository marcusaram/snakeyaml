/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.yaml.snakeyaml.emitter;

import java.io.IOException;

import org.yaml.snakeyaml.events.Event;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Emitter {
    public void emit(Event event) throws IOException;
}
