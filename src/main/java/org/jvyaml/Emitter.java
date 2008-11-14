/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import org.yaml.snakeyaml.events.Event;

/**
 * @see PyYAML 3.06 for more information
 */
public interface Emitter {
    void emit(final Event event) throws IOException;
}
