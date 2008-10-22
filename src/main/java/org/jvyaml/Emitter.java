/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml;

import java.io.IOException;

import org.jvyaml.events.Event;

/**
 * @see PyYAML for more information
 */
public interface Emitter {
    void emit(final Event event) throws IOException;
}// Emitter
