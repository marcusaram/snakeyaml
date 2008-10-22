/*
 * See LICENSE file in distribution for copyright and licensing information.
 */
package org.jvyaml.events;

/**
 * @author <a href="mailto:ola.bini@ki.se">Ola Bini</a>
 * @version $Revision: 1.1 $
 */
public class AliasEvent extends NodeEvent {
    public AliasEvent(final String anchor) {
        super(anchor);
    }
}// AliasEvent
