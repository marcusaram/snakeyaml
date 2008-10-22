/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @see PyYAML for more information
 */
public class AliasEvent extends NodeEvent {
    public AliasEvent(final String anchor) {
        super(anchor);
    }
}
